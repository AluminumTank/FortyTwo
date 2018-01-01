# FORTY-TWO
This program was developed in several days for a university data structures course to meet the following problem specification:

Develop a Java package which implements an ADT for a (2,4) search tree. This search tree is an example of a more general tree called a multiway search tree.
In a multiway search tree, an internal node can have more than 2 children. For a (2,4) tree, an internal node can have a minimum of 2 children and a maximum of 4. Also, for an internal node, the amount of key-value pairs, or (k,v) pairs, is n - 1 where n is the number of children of the node. For external nodes, the node has a minimum of 1 and a maximum of 3 (k,v) pairs.

As a search tree, the data will be ordered by the keys. For a node w, we will say that w.data[] refers to an array of (k,v) pairs, while w.children[] refers to an array of pointers to the node's children. Given this convention, we can say that w.data[i] < w.data[i + 1]. If a number is greater than w.data[i] but less than w.data[i + 1], then that piece of data is passed on to the node at w.children[i + 1], where we run the same check again. If, when going through this search, we come to an external node, we do a shifting insert at w.data[i + 1].

## TODO
