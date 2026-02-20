package com.develhack.ddiff;

import java.io.InputStream;
import java.util.jar.Manifest;

import picocli.CommandLine.IVersionProvider;

public class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        try (InputStream manifestStream = getClass().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF")) {
            Manifest manifest = new Manifest(manifestStream);
            String version = manifest.getMainAttributes().getValue("Implementation-Version");
            return new String[] { version };
        } catch (Exception e) {
            return new String[] { "UNKNOWN" };
        }
    }
}
