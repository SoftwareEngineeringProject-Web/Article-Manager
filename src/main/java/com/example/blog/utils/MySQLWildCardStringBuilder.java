package com.example.blog.utils;

public class MySQLWildCardStringBuilder {
  private String string;

  public MySQLWildCardStringBuilder(String string) {
    this.string = string;
  }

  public String build() {
    StringBuilder builder = new StringBuilder();
    for (char c : string.toCharArray()) {
      builder.append('%');
      switch (c) {
        case '%': case '/': case '_':
          builder.append('/');
        default:
          builder.append(c);
      }
    }
    builder.append('%');
    return builder.toString();
  }
}
