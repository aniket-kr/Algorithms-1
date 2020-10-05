Algorithms 1
============
This repo contains Java implementations of all data structures and 
associated algorithms that were I learned during the excellent Coursera 
course [Algorithms, Part I](https://www.coursera.org/learn/algorithms-part1),
offered by the Princeton University.

Package Structure
-----------------
The implementations in the project have been organised in packages. A
_textual directory map_ of the project looks like the following:

> Files with incomplete implementations have been marked with
> an asterisk `*`.

```
com.company.aniketkr.algorithms1
  |-- WeightedQuickUnion
  |
  |-- collection
  |     |-- *Collection (interface)
  |     |
  |     |-- list
  |     |     |-- *List (interface)
  |     |     |-- *ArrayList
  |     |     `-- *LinkedList
  |     |
  |     |-- queue
  |     |     |-- *Queue (interface)
  |     |     |-- *ArrayQueue
  |     |     `-- *LinkedQueue
  |     |
  |     |-- stack
  |     |     |-- *Stack (interface)
  |     |     |-- *ArrayStack
  |     |     `-- *LinkedStack
  |     |
  |     |-- dequeue
  |     |     |-- *Deque (interface)
  |     |     |-- *ArrayDeque
  |     |     `-- *LinkedDeque
  |     |
  |     `-- priorityqueue
  |           |-- *PriorityQueue (interface)
  |           `-- *HeapPriorityQueue
  |
  |-- sorting
  |     |
  |     |-- *Sorter (package-private abstract)
  |     |-- *Selection
  |     |-- *Bubble
  |     |-- *Insertion
  |     |-- *Shell
  |     |-- *Merge
  |     |-- *Quick
  |     `-- *Heap
  |
  `-- map
        |-- Map (abstract)
        |-- OrderMap (abstract)
        |-- Entry (immutable helper type)
        |
        |-- hash
        |     |-- ChainingHashMap
        |     `-- *ProbingHashMap
        |
        `-- symbol
              |-- UnorderedMap
              |-- OrderedMap
              `-- *RedBlackMap
```

Code Style
----------
This project follows
[Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
The styles specified have been followed strictly.

API Notes
---------
 + `Collection` interface is the base interface of all data structures
   that hold a single type of element.
 
 + `List`, `Queue`, `Stack`, `Deque` and `PriorityQueue` interfaces, all
   extend the base `Collection` interface. Note that `Map` does __NOT__
   extend this interface.

 + `Map` is another (different) interface that is the base class for data
   structures that associate a given "key" to the provided "value".

 + `OrderMap` extends `Map` interface to take advantage of the fact that
   the "keys" can be ordered, and hence provides an extended API that can
   be implemented efficiently.

 + The abstract class `Sorter` is package-private but provided a base 
   class to inherit certain overloads of the `sort()` method. All other
   public classes in package `sorting` inherit the base class `Sorter`.

Implementation Notes
--------------------
 + The implementation of `map.symbol.RedBlackMap` is based on a particular
   variant of the red-black binary search tree, the 
   _Left-Leaning Red-Black 2-3 Tree_. Read more about it in the
   [research paper](https://www.cs.princeton.edu/~rs/talks/LLRB/LLRB.pdf).

 + All generic types that require comparison of "elements" or "keys" accept
   an optional `java.util.Comparator` object which will be used for the
   comparison. If not provided, an attempt to use the
   `Comparable.compareTo()` will be made at __Runtime__. Java erasure 
   makes it impossible to do otherwise without accepting `Class<?>` as a
   parameter, which is unnecessary (and looks ugly).
