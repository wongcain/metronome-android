package test;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FileFsFile;
import org.robolectric.util.Logger;
import org.robolectric.util.ReflectionHelpers;

/**
 * More dynamic path resolution.
 *
 * This workaround is only for Mac Users necessary and only if they don't use the $MODULE_DIR$
 * workaround mentioned at http://robolectric.org/getting-started/.
 *
 * Follow this issue at https://code.google.com/p/android/issues/detail?id=158015
 */
public class CustomRobolectricRunner extends RobolectricTestRunner {
    private static final String BUILD_OUTPUT = "build/intermediates";

    public CustomRobolectricRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        if (config.constants() == Void.class) {
            Logger.error("Field 'constants' not specified in @Config annotation");
            Logger.error("This is required when using RobolectricGradleTestRunner!");
            throw new RuntimeException("No 'constants' field in @Config annotation!");
        }

        final String type = getType(config);
        final String flavor = getFlavor(config);
        final String packageName = getPackageName(config);
        final String buildOutput = getBuildOutputPath(config);

        final FileFsFile res;
        final FileFsFile assets;
        final FileFsFile manifest;

        // res/merged added in Android Gradle plugin 1.3-beta1
        if (FileFsFile.from(buildOutput, "res", "merged").exists()) {
            res = FileFsFile.from(buildOutput, "res", "merged", flavor, type);
        } else if (FileFsFile.from(buildOutput, "res").exists()) {
            res = FileFsFile.from(buildOutput, "res", flavor, type);
        } else {
            res = FileFsFile.from(buildOutput, "bundles", flavor, type, "res");
        }

        if (FileFsFile.from(buildOutput, "assets").exists()) {
            assets = FileFsFile.from(buildOutput, "assets", flavor, type);
        } else {
            assets = FileFsFile.from(buildOutput, "bundles", flavor, type, "assets");
        }

        if (FileFsFile.from(buildOutput, "manifests").exists()) {
            manifest = FileFsFile.from(buildOutput, "manifests", "full", flavor, type, "AndroidManifest.xml");
        } else {
            manifest = FileFsFile.from(buildOutput, "bundles", flavor, type, "AndroidManifest.xml");
        }

        Logger.debug("CustomRobolectricRunner assets directory: " + assets.getPath());
        Logger.debug("CustomRobolectricRunner res directory: " + res.getPath());
        Logger.debug("CustomRobolectricRunner manifest path: " + manifest.getPath());
        Logger.debug("CustomRobolectricRunner package name: " + packageName);
        return new AndroidManifest(manifest, res, assets, packageName);
    }

    private String getModuleRootPath(Config config) {
        String moduleRoot = config.constants().getResource("").toString().replace("file:", "").replaceAll("%20", "\\ ");
        return moduleRoot.substring(0, moduleRoot.indexOf("/build"));
    }

    private String getBuildOutputPath(Config config) {
        return getModuleRootPath(config) + "/build/intermediates";
    }

    private static String getType(Config config) {
        try {
            return ReflectionHelpers.getStaticField(config.constants(), "BUILD_TYPE");
        } catch (Throwable e) {
            return null;
        }
    }

    private static String getFlavor(Config config) {
        try {
            return ReflectionHelpers.getStaticField(config.constants(), "FLAVOR");
        } catch (Throwable e) {
            return null;
        }
    }

    private static String getPackageName(Config config) {
        try {
            final String packageName = config.packageName();
            if (packageName != null && !packageName.isEmpty()) {
                return packageName;
            } else {
                return ReflectionHelpers.getStaticField(config.constants(), "APPLICATION_ID");
            }
        } catch (Throwable e) {
            return null;
        }
    }

}
