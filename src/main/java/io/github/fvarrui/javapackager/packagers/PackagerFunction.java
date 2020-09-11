package io.github.fvarrui.javapackager.packagers;

import java.io.File;
import java.util.function.Function;

public interface PackagerFunction extends Function<Packager, File> {

}
