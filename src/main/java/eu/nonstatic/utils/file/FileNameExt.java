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

import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

public record FileNameExt(String name, String ext) {

  public static final char SEPARATOR = '.';

  public static final Pattern TO_FILENAME_PATTERN_UNIX = Pattern.compile("[/\\x00]+"); // https://stackoverflow.com/a/31976060
  public static final Pattern TO_FILENAME_PATTERN_GENERIC = Pattern.compile("[<>:\"/\\\\|?*\\x00-\\x1f\\x7f]+");
  public static final String REPLACEMENT = "_";

  public boolean isName() {
    return name != null;
  }

  public boolean isExt() {
    return ext != null;
  }


  public static FileNameExt of(Path path) {
    return of(path.getFileName().toString());
  }

  public static FileNameExt of(String fileName) {
    String name = null;
    String ext = null;

    if (fileName != null) {
      int dotpos = fileName.lastIndexOf('.');
      if (dotpos >= 0) {
        name = fileName.substring(0, dotpos);
        ext = fileName.substring(dotpos + 1);
      } else {
        name = fileName;
      }
    }
    return new FileNameExt(name, ext);
  }


  public FileNameExt withName(String name) {
    return new FileNameExt(name, ext);
  }

  public FileNameExt withExt(String ext) {
    return new FileNameExt(name, ext);
  }

  public FileNameExt withLowerExt() {
    return new FileNameExt(name, ext != null ? ext.toLowerCase(Locale.ROOT) : null);
  }

  public FileNameExt withUpperExt() {
    return new FileNameExt(name, ext != null ? ext.toUpperCase(Locale.ROOT) : null);
  }

  public static String ext(String fileName) {
    return of(fileName).ext();
  }

  public static String ext(Path file) {
    String result = null;
    if (file != null) {
      result = ext(file.getFileName().toString());
    }
    return result;
  }

  public boolean isExtIgnoreCase(String ext) {
    if(this.ext == null) {
      return ext == null;
    } else {
      return this.ext.equalsIgnoreCase(ext);
    }
  }

  /**
   * @return the unescaped name.ext concatenation as a CharSequence. Useful with URLs.
   */
  public CharSequence join() {
    if(name == null && ext == null) {
      return null;
    }

    StringBuilder sb = new StringBuilder();
    if (name != null) {
      sb.append(name);
    }
    if(ext != null) {
      sb.append(SEPARATOR).append(ext);
    }
    return sb.toString();
  }

  public String toFileName() {
    CharSequence cs = join();
    return cs != null ? sanitizeFileNameGeneric(cs) : null;
  }

  private static String sanitizeFileName(CharSequence cs, Pattern pattern) {
    return pattern.matcher(cs).replaceAll(REPLACEMENT).trim();
  }

  public static String sanitizeFileNameGeneric(CharSequence cs) {
    return sanitizeFileName(cs, TO_FILENAME_PATTERN_GENERIC);
  }

  public static String sanitizeFileNameUnix(CharSequence cs) {
    return sanitizeFileName(cs, TO_FILENAME_PATTERN_UNIX);
  }
}
