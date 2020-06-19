package com.example.a5_in_a_row_app;

import android.util.Pair;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Keeps a history of actions that have been done and undone using two stacks. When an item is done,
 * it is pushed onto the undo stack. When an item is undone, it is popped from the undo stack and
 * pushed to the redo stack. The number of history items is limited by the capacity.
 */
public class StackHistory {
    /** Data structures for tracking undo/redo events. */
    private final Deque<Pair<Integer,Integer>> undoStack, redoStack;
    /** Should always be true that undoStack.size() + redoStack.size() <= capacity. */
    private final int capacity;

    public StackHistory() {
        this(10);
    }
    public StackHistory(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(("Illegal capacity: " + capacity));
        }
        this.capacity = capacity;

        undoStack = new LinkedList<>();
        redoStack = new LinkedList<>();
    }

    /**
     * Add a reversible event to the history.
     *
     * @param p    the location of chess piece to be added.
     */
    public void addAction(Pair<Integer, Integer> p) {
        //  support addAction
        // 1. If the stack is full, remove the oldest thing in it
        // 2. Add the new event to the undo stack
        // 3. Clear out the redo stack (when we do a new action we have to delete all the redo
        // actions to ensure consistency)
        if (undoStack.size() >= capacity) {
            undoStack.removeFirst();
        }
        undoStack.addLast(p);
        redoStack.clear();
    }

    /**
     * Undoes an action.
     *
     * @return null if there is nothing to undo, otherwise the action to be undone.
     */
    public Pair<Integer, Integer> undo() {
        //  support undo
        // 1. If the undo stack is empty return null
        // 2. Otherwise remove the most recent action from the stack
        // 2.1. Add it to the redo stack
        // 2.2. Return it.
        if (undoStack.isEmpty()) {
            return null;
        }
        Pair<Integer, Integer> a = undoStack.removeLast();
        redoStack.addLast(a);
        return a;
    }

    /**
     * Redoes an action.
     *
     * @return null if there is nothing to redo, otherwise the action to be redone.
     */
    
    public Pair<Integer, Integer> redo() {
        //  support redo
        // 1. If the redo stack is empty return null
        // 2. Otherwise get the most recent action from the stack
        // 2.1. Add it to the undo stack
        // 2.2. Return it.
        if (redoStack.isEmpty()) {
            return null;
        }
        Pair<Integer, Integer> a = redoStack.removeLast();
        undoStack.addLast(a);
        return a;
    }

    /**
     * Clears the history.
     */
    
    public void clear() {
        // clear the datastructures
        undoStack.clear();
        redoStack.clear();
    }

    /**
     * Is there anything that can be undone?
     *
     * @return True if can undo any actions, false otherwise.
     */
    
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Is there anything that can be done?
     *
     * @return True if can redo any actions, false otherwise.
     */
    
    public boolean canRedo() {return !redoStack.isEmpty();}

    public String toString() {
        return  "Undo size: " + undoStack.size() + ", redo size: " + redoStack.size();
    }
}
