package model;

/**
 * Singly linked list in the style used in the course (current pointer).
 * Besides the usual operations, it keeps a tail pointer (O(1) append) and a
 * previous pointer (O(1) removal of the current node).
 * NOTE: if your course version differs, keep these method names so the rest
 * of the team's code keeps working.
 * @param <T> the type of the elements
 */
public class LinkedList<T> {
    private Node<T> head;     // first node
    private Node<T> tail;     // last node
    private Node<T> current;  // current node
    private Node<T> previous; // node before current (null if current is head)
    private int size;         // number of elements

    public LinkedList() {
        head = tail = current = previous = null;
        size = 0;
    }

    /** @return true if the list has no elements. */
    public boolean empty() {
        return head == null;
    }

    /** @return true if current is the last node. */
    public boolean last() {
        return current == tail;
    }

    /** @return always false (a linked list is never full). */
    public boolean full() {
        return false;
    }

    /** @return the number of elements. */
    public int size() {
        return size;
    }

    /** Moves current to the first node. */
    public void findFirst() {
        current = head;
        previous = null;
    }

    /** Moves current to the next node (stays put on the last node). */
    public void findNext() {
        if (current != null && current.next != null) {
            previous = current;
            current = current.next;
        }
    }

    /** @return the element at current (null if list is empty). */
    public T retrieve() {
        return (current == null) ? null : current.data;
    }

    /** Replaces the element at current. */
    public void update(T data) {
        if (current != null) {
            current.data = data;
        }
    }

    /** Inserts a new element at the very beginning; current = new node. */
    public void insertAtFirst(T data) {
        Node<T> n = new Node<T>(data);
        n.next = head;
        head = n;
        if (tail == null) {
            tail = n;
        }
        current = n;
        previous = null;
        size++;
    }

    /** Inserts a new element at the end in O(1); current = new node. */
    public void addLast(T data) {
        Node<T> n = new Node<T>(data);
        if (head == null) {
            head = tail = n;
            previous = null;
        } else {
            tail.next = n;
            previous = tail;
            tail = n;
        }
        current = n;
        size++;
    }

    /** Inserts a new element right AFTER current; current = new node. */
    public void insert(T data) {
        if (head == null) {
            addLast(data);
            return;
        }
        Node<T> n = new Node<T>(data);
        n.next = current.next;
        current.next = n;
        if (current == tail) {
            tail = n;
        }
        previous = current;
        current = n;
        size++;
    }

    /** Inserts a new element right BEFORE current; current = new node. */
    public void insertBeforeCurrent(T data) {
        if (head == null || previous == null) {
            insertAtFirst(data);
            return;
        }
        Node<T> n = new Node<T>(data);
        n.next = current;
        previous.next = n;
        current = n;
        size++;
    }

    /**
     * Removes the current node. Afterwards current points to the node that
     * followed the removed one (or to the head if the removed node was last).
     */
    public void remove() {
        if (current == null) {
            return;
        }
        if (previous == null) {
            head = head.next;
        } else {
            previous.next = current.next;
        }
        if (current == tail) {
            tail = previous;
        }
        size--;
        if (head == null) {
            current = previous = tail = null;
            return;
        }
        Node<T> next = (previous == null) ? head : previous.next;
        if (next == null) {
            current = head;
            previous = null;
        } else {
            current = next;
        }
    }
}
