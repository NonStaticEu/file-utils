/**
 * File-Utils
 * Copyright (C) 2026 NonStatic
 *
 * This file is part of file-utils.
 * file-utils is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with . If not, see <https://www.gnu.org/licenses/>.
 */
package eu.nonstatic.utils.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public final class Lister {

  private Lister() {}

  public static List<Path> listFiles(Path dir, Predicate<Path> typeTester, boolean deep) throws IOException {
    var files = new LinkedList<Path>();
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
      for (Path file : ds) {
        if (Files.isDirectory(file) && deep) {
          files.addAll(listFiles(file, typeTester, deep));
        } else if (Files.isRegularFile(file) && typeTester.test(file)) {
          files.add(file);
        }
      }
    }

    return files;
  }


  public static List<Path> listDirs(Path dir, boolean self, boolean deep) throws IOException {
    var dirs = new LinkedList<Path>();
    if (self) {
      dirs.add(dir);
    }
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
      for (Path file : ds) {
        if (Files.isDirectory(file)) {
          dirs.add(file);
          if(deep) {
            dirs.addAll(listDirs(file, false, deep));
          }
        }
      }
    }

    return dirs;
  }

}
