package com.example.blog.data;

import java.util.ArrayList;
import java.util.List;

public class CategoryTreeNode {
  private ArrayList<CategoryTreeNode> children;
  private Long id;
  private String name;

  public CategoryTreeNode(Long id, String name) {
    this.id = id;
    this.name = name;
    this.children = new ArrayList<CategoryTreeNode>();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void addChild(CategoryTreeNode child) {
    children.add(child);
  }

  public List<CategoryTreeNode> getChildren() {
    return children;
  }
}
