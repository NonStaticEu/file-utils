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
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.SecureRandom;

public final class FileUtils {

  private FileUtils() {}

  /**
   * Case-insensitive FS proof file rename/move
   */
  public static void move(Path oldPath, Path newPath) throws IOException {
    if(isCaseInsensitiveFileSystem(oldPath.getFileSystem())) {
      // Forcing damn Windows to accept a renaming where only the case is changed
      Path tempFile = createSiblingVariant(oldPath);
      Files.move(oldPath, tempFile);
      Files.move(tempFile, newPath);
    } else {
      Files.move(oldPath, newPath);
    }
  }


  private static Path createSiblingVariant(Path path) {
    var rnd = new SecureRandom();
    Path variant;
    do {
      String prefix = Integer.toUnsignedString(rnd.nextInt());
      variant = path.resolveSibling(prefix + '-' + path.getFileName());
    } while (Files.exists(variant));

    return variant;
  }

  public static FileTime getLastModifiedTimeDeep(Path dir) throws IOException {
    FileTime lastModifiedTime = Files.getLastModifiedTime(dir);
    try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
      for (Path file : ds) {
        FileTime lmt = Files.isDirectory(file) ? getLastModifiedTimeDeep(file) : Files.getLastModifiedTime(file);
        if(lmt.compareTo(lastModifiedTime) > 0) {
          lastModifiedTime = lmt;
        }
      }
    }
    return lastModifiedTime;
  }

  public static boolean isCaseInsensitiveFileSystem(FileSystem fs) {
    String suffix;
    Path lower;
    var rnd = new SecureRandom();
    do {
      suffix = Long.toUnsignedString(rnd.nextLong());
      lower = fs.getPath("cics" + suffix);
    } while (Files.exists(lower));

    // Not fs.provider().isSameFile() that might try and access the files
    return lower.equals(fs.getPath("CICS" + suffix));
  }
}
