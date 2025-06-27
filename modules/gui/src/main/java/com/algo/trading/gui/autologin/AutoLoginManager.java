package com.algo.trading.gui.autologin;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.algo.trading.gui.util.Utilities.dumpDom;
import static com.algo.trading.gui.util.Utilities.strReplace;

/**
 * Fills UID / PWD once, then keeps typing a fresh 6-digit TOTP every
 * 0.5 s until the page navigates away or 30 s have elapsed.
 */
@Slf4j
@RequiredArgsConstructor
public class AutoLoginManager {

    private final WebEngine engine;
    private final String uid;
    private final String pwd;
    private final String totpSeed;

    public void register() {
        engine.getLoadWorker()
                .stateProperty()
                .addListener((obs, oldState, newState) -> {
                    if (newState != Worker.State.SUCCEEDED) return;
                    if (onUidPwdPage()) {
                        injectUidPwd();
                        startTotpLoop();
                    }
                });
    }

    /* ---------- login page helpers -------------------------- */

    private boolean onUidPwdPage() {
        return (Boolean) engine.executeScript("document.getElementById('userid') !== null");
    }

    private void injectUidPwd() {
        log.debug("Injecting UID/PWD");
        engine.executeScript("""
                (()=>{
                  let u=document.getElementById('userid');
                  let p=document.getElementById('password');
                  if(!u||!p) return;
                  u.value='%s'; p.value='%s';
                  u.dispatchEvent(new Event('input'));
                  p.dispatchEvent(new Event('input'));
                  (document.querySelector('button[type=submit],input[type=submit]')||{}).click();
                })();
                """.formatted(strReplace(uid), strReplace(pwd)));
    }

    /* -------------------- Step-2 looping TOTP -------------------- */

    private void startTotpLoop() {

        final int[] tick = {0};
        final int maxTicks = 60;          // 30 s

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500), ev -> {

            tick[0]++;

            /* bail-out after 30 s */
            if (tick[0] > maxTicks) {
                log.warn("Gave up after 30 s – DOM dump follows");
                dumpDom(engine.getDocument());
                timeline.stop();
                return;
            }

            /* stop if we already left the TOTP screen */
            boolean stillOnTotp = (Boolean) engine.executeScript(
                    "document.querySelector('input[name=totp],#totp,input[name=otp],input[maxlength=\"6\"],input[type=tel]') !== null");
            if (!stillOnTotp) {
                log.debug("TOTP page closed – loop stops");
                timeline.stop();
                return;
            }

            String otp = String.valueOf(new GoogleAuthenticator().getTotpPassword(totpSeed));
            log.trace("Typing OTP {} (attempt {})", otp, tick[0]);

            engine.executeScript("""
                    (()=>{
                       let f =
                         document.getElementById('totp') ||
                         document.querySelector('input[name=totp]') ||
                         document.querySelector('input[name=otp]')  ||
                         document.querySelector('input[maxlength="6"]') ||
                         document.querySelector('input[type=tel]');
                       if(!f) return;
                       f.value='%s';
                       f.dispatchEvent(new Event('input'));
                       (document.querySelector('button[type=submit],input[type=submit]')||{}).click();
                    })();
                    """.formatted(otp));
        }));
        timeline.play();
    }
}
