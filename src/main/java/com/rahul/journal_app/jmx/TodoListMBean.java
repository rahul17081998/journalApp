package com.rahul.journal_app.jmx;

import java.util.List;

public interface TodoListMBean {
    public String listAllTodos();
    public void add(String todo);
    public String delete(String todo);
    public String getLatestTodo();
    public List<String> getTodos();
    public int getTodoSize();
}
