package net.jsign;

import io.github.fvarrui.javapackager.model.WindowsSigning;
import io.github.fvarrui.javapackager.utils.Logger;

import java.io.File;

public class WindowsSigner {

    private static final String TIMESTAMPING_AUTHORITY = "http://timestamp.comodoca.com/authenticode";
    private static final Console CONSOLE = new Console() {
        @Override
        public void debug(String message) {
            Logger.debug(message);
        }

        public void info(String message) {
            Logger.info(message);
        }

        @Override
        public void warn(String message) {
            Logger.warn(message);
        }

        @Override
        public void warn(String message, Throwable throwable) {
            Logger.warn(message + " (" + throwable.getMessage() + ")");
        }

        public void error(String message) {
            Logger.error(message);
        }
    };

    // SINGLETON

    public static void sign(File file, String displayName, String url, WindowsSigning signing) {
        if (signing == null) {
            Logger.warn("No signing configuration found");
            return;
        }
        Logger.infoIndent("Signing " + file);
        try {
            SignerHelper helper = new SignerHelper(CONSOLE, "");
            helper.name(displayName);
            helper.url(url);
            helper.alg(signing.getAlg());
            helper.keystore("" + signing.getKeystore());
            helper.storepass(signing.getStorepass());
            helper.storetype(signing.getStoretype());
            helper.alias(signing.getAlias());
            helper.certfile(signing.getCertfile());
            helper.keyfile(signing.getKeyfile());
            helper.keypass(signing.getKeypass());
            helper.tsaurl(TIMESTAMPING_AUTHORITY);
            helper.sign(file);
            Logger.infoUnindent("Signed " + file);
        } catch (net.jsign.SignerException e) {
            Logger.errorUnindent(file + " could not be signed", e);
        }
    }

}
