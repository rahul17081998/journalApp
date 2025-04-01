package com.rahul.journal_app.jmx;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TodoList implements TodoListMBean{
    private static List<String> todos = new ArrayList<>();

    public TodoList(){
        todos.add("Buy milk");
        todos.add("Buy Momo");
        todos.add("Grass Cutting");
    }


//    @Override
//    public String listAllTodos() {
//        return todos.stream().collect(Collectors.joining("\n"));
//    }
//
//    @Override
//    public void add(String todo) {
//        todos.add(todo);
//    }
//
//    @Override
//    public String delete(String todo) {
//        todos.remove(todo);
//        return listAllTodos();
//    }
//
//    @Override
//    public String getLatestTodo() {
//        return todos.get(todos.size()-1);
//    }
//
//    @Override
//    public List<String> getTodos() {
//        return todos;
//    }

    @Override
    public int getTodoSize() {
        return todos.size();
    }
}

