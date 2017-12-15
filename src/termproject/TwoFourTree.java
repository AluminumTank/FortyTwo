package termproject;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Title:        Term Project 2-4 Trees
 * Description: An abstract data type for a 2-4 tree
 * Copyright:    Copyright (c) 2017
 * @author Joel Beckmeyer & Daniel Parker
 * @version 1.0
 */
public class TwoFourTree
        implements Dictionary {

    private static final int MAX_ITEMS = 3;

    private Comparator treeComp;
    private int size = 0;
    private TFNode treeRoot = null;

    public TwoFourTree(Comparator comp) {
        treeComp = comp;
    }

    private TFNode root() {
        return treeRoot;
    }

    private void setRoot(TFNode root) {
        treeRoot = root;
    }

	/**
	 * Searches the tree for a node containing the given key.
	 * 
	 * @param key Object to search for
	 * @return node which contains key, or insertion point for this key
	 * @throws TwoFourTreeException if root is null
	 */
	private TFNode search(Object key) throws TwoFourTreeException {
		TFNode current = treeRoot;
		TFNode parent = null;
		if(treeRoot == null) {
			throw new TwoFourTreeException("root was null");
		}

		// loop until we have reached the child of an external node, or until
		// we find the key
		while(current != null) {
			int index = FFGTE(current, key);

			// ensure that the index given is not out of bounds
			if(index != current.getNumItems()) {
				if(treeComp.isEqual(current.getItem(index).key(), key)) {
					break;
				}
			}
				
			parent = current;
			current = current.getChild(index);
		}

		// if key was not found, we know we are at an external node, so we must
		// return that node rather than its null "child"
		if(current == null) {
			return parent;
		}else {
			return current;
		}
	}

	/**
	 * Finds the index of the first item that is greater than or equal to the 
	 * given key.
	 * 
	 * @param node TFnode to be searched
	 * @param key key to find
	 * @return index of first item greater than or equal to given key
	 */
	private int FFGTE(TFNode node, Object key) {
		int i;
		// loop through item array, comparing each item until we find first item
		// greater than or equal to key
		for(i = 0; i < node.getNumItems(); i++) {
			if(treeComp.isGreaterThanOrEqualTo(node.getItem(i).key(), key)) {
				break;
			}
		}
		return i;
	}

	/**
	 * Finds the index of the given node in its parent.
	 * 
	 * @param node the node to be found in the parent
	 * @return index of node in its parent
	 */
	private int WCIT(TFNode node) {
		TFNode parent = node.getParent();
		int i;
		// loop through child array until we find the given node
		for(i = 0; i < parent.getNumItems() + 1; ++i) {
			if(parent.getChild(i) == node) {
				break;
			}
		}
		return i;
	}

	/**
	 * Finds the in-order successor of the given node-key combination.
	 * 
	 * @param node the node to start at
	 * @param key the key to follow
	 * @return the in-order successor node
	 */
	private TFNode getInOrderSuccessor(TFNode node, int index) {
		TFNode parent = null;
		// go down the right child of our key
		TFNode current = node.getChild(index + 1);

		// go left until we hit a leaf
		while(current != null) {
			parent = current;
			current = current.getChild(0);
		}

		return parent;
	}

	/**
	 * Checks for and fixes node overflow.
	 * 
	 * @param node the node to check for overflow
	 */
	private void fixOverflow(TFNode node) {
		if(node.getNumItems() <= MAX_ITEMS) {
			return;
		}

		TFNode parent = node.getParent();

		// special case when root overflows (we must increase height of tree)
		if(parent == null) {
			parent = new TFNode();
			parent.setChild(0, node);
			node.setParent(parent);
			treeRoot = parent;
		}

		// removes offending data from current node
		int index = WCIT(node);

		// preserves data that we want to move around
		TFNode left = node.getChild(3);
		node.setChild(3, null);
		TFNode right = node.getChild(4);
		node.setChild(4, null);
		Item toSibling = node.deleteItem(3);
		Item toParent = node.deleteItem(2);
		
		// creates and hooks up new sibling
		TFNode sibling = new TFNode();
		sibling.setParent(parent);
		sibling.addItem(0, toSibling);
		sibling.setChild(0, left);
		sibling.setChild(1, right);
		if(left != null) {
			left.setParent(sibling);
			right.setParent(sibling);
		}
		
		// connects children to parents
		parent.insertItem(index, toParent);
		parent.setChild(index, node);
		parent.setChild(index + 1, sibling);
		//}
		
		// recursively call on parent
		fixOverflow(parent);
	}

	/**
	 * Checks for and fixes node underflow.
	 * 
	 * @param node the node to check for underflow
	 */
	private void fixUnderflow(TFNode node) {
		// checks for underflow
		if(node.getNumItems() < 1) {

			// special case where root is underflowed
			if(node == treeRoot) {
				treeRoot  = node.getChild(0);
				node.setParent(null);
			}

			// different cases to check and run
			else if(isPossibleLTrans(node)) {
				leftTransfer(node);
			}
			else if(isPossibleRTrans(node)) {
				rightTransfer(node);
			}
			else if(isPossibleLFusion(node)) {
				leftFusion(node);
			}
			else {
				rightFusion(node);
			}
		}
	}

	/**
	 * Checks if left transfer is possible.
	 * 
	 * @param node node to check for possible transfer
	 * @return true if possible
	 */
	private boolean isPossibleLTrans(TFNode node) {
		// checks if the given node has a left sibling
		int index = WCIT(node);
		TFNode parent = node.getParent();

		if(index > 0) {
			TFNode sibling = parent.getChild(index - 1);

			// checks if existing left sibling has 2+ items
			return sibling.getNumItems() >= 2;
		}else {
			return false;
		}
	}

	/**
	 * Checks if right transfer is possible.
	 * 
	 * @param node node to check for possible transfer
	 * @return true if possible
	 */
	private boolean isPossibleRTrans(TFNode node) {
		// checks if the given node has a right sibling
		int index = WCIT(node);
		TFNode parent = node.getParent();

		if(index < parent.getNumItems()) {
			TFNode sibling = parent.getChild(index + 1);

			// checks if existing right sibling has 2+ items
			return sibling.getNumItems() >= 2;
		}else {
			return false;
		}
	}

	/**
	 * Performs a left transfer operation.
	 * 
	 * @param node underflowed node to perform on
	 */
	private void leftTransfer(TFNode node) {
		int index = WCIT(node);
		TFNode parent = node.getParent();
		TFNode sibling = parent.getChild(index - 1);

		// preserving data that would otherwise be lost
		TFNode lastChild = sibling.getChild(sibling.getNumItems());
		sibling.setChild(sibling.getNumItems(), null);
		Item lastItem = sibling.deleteItem(sibling.getNumItems() - 1);

		// swap old sibling item with parent and add parent item to underflowed
		// node
		Item parentItem = parent.replaceItem(index - 1, lastItem);
		node.addItem(0, parentItem);


		// move old child 0 to child position 1, then add child from sibling as
		// child 0
		TFNode oldChild = node.getChild(0);
		node.setChild(1, oldChild);
		node.setChild(0, lastChild);
		if(lastChild != null) {
			lastChild.setParent(node);
		}
	}
	
	/**
	 * Performs a right transfer operation.
	 * 
	 * @param node underflowed node to perform on
	 */
	private void rightTransfer(TFNode node) {
		int index = WCIT(node);
		TFNode parent = node.getParent();
		TFNode sibling = parent.getChild(index + 1);

		// preserving data that would otherwise be lost
		TFNode firstChild = sibling.getChild(0);
		Item firstItem = sibling.removeItem(0);

		// swap old sibling item with parent and add parent item to underflowed
		// node
		Item parentItem = parent.replaceItem(index, firstItem);
		node.addItem(0, parentItem);


		// insert child from sibling as 2nd(index 1) child
		node.setChild(1, firstChild);
		if(firstChild != null) {
			firstChild.setParent(node);
		}
	}

	/**
	 * Checks if left fusion operation is possible
	 * 
	 * @param node node to check for possible fusion
	 * @return true if possible
	 */
	private boolean isPossibleLFusion(TFNode node) {
		// checks if a left sibling exists
		return WCIT(node) > 0;
	}

	/**
	 * Performs a left fusion operation
	 * 
	 * @param node underflowed node to perform fusion on
	 */
	private void leftFusion(TFNode node) {
		int index = WCIT(node);
		TFNode parent = node.getParent();

		// delete underflowed node
		parent.setChild(index, null);

		// preserving data
		TFNode sibling = parent.getChild(index - 1);
		Item parentItem = parent.removeItem(index - 1);
		TFNode child = node.getChild(0);

		// insert data in sibling
		sibling.insertItem(sibling.getNumItems(), parentItem);
		sibling.setChild(sibling.getNumItems(), child);
		if(child != null) {
			child.setParent(sibling);
		}

		// fix parent pointer
		parent.setChild(index - 1, sibling);
		if(node == null) {
			System.out.println("node was null: ");
		}

		// recursively check underflow on parent
		fixUnderflow(parent);
	}

	/**
	 * Performs a right fusion operation
	 * 
	 * @param node underflowed node to perform fusion on
	 */
	private void rightFusion(TFNode node) {
		int index = WCIT(node);
		TFNode parent = node.getParent();

		// preserving data
		Item parentItem = parent.removeItem(index);
		TFNode child = node.getChild(0);
		TFNode sibling = parent.getChild(index);

		// insert data in sibling
		sibling.insertItem(0, parentItem);
		sibling.setChild(0, child);
		if(child != null) {
			child.setParent(sibling);
		}

		// recursively check underflow on parent
		fixUnderflow(parent);
	}
    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * Searches dictionary to determine if key is present
     * @param key to be searched for
     * @return object corresponding to key; null if not found
     */
    public Object findElement(Object key) {
		// first get the node which might contain the given key
		TFNode target = search(key);

		// find the key in this node
		for(int i = 0; i < target.getNumItems(); ++i) {
			if(treeComp.isEqual(target.getItem(i).key(), key)) {
				return target.getItem(i).element();
			}
		}
		
		// if key was not in node, return null
		return null;
    }

    /**
     * Inserts provided element into the Dictionary
     * @param key of object to be inserted
     * @param element to be inserted
     */
    public void insertElement(Object key, Object element) {
		if(treeRoot == null) {
			treeRoot = new TFNode();
		}

		Item data = new Item(key, element);
		TFNode node = search(key);
		int index = FFGTE(node, key);
		if(index != node.getNumItems()) {
			// external node that contains a duplicate
			if(node.getChild(0) == null) {
				node.insertItem(index, data);
			// internal node that contains a duplicate
			}else {
				node = getInOrderSuccessor(node, index);
				node.insertItem(0, data);
			}
		// if we are at last index
		}else {
			node.insertItem(index, data);
		}

		fixOverflow(node);
    }

    /**
     * Searches dictionary to determine if key is present, then
     * removes and returns corresponding object
     * @param key of data to be removed
     * @return object corresponding to key
     * @exception ElementNotFoundException if the key is not in dictionary
     */
    public Object removeElement(Object key) throws ElementNotFoundException {
		TFNode node = search(key);
		int index = FFGTE(node, key);

		// if we are at an external node and we got the last index of node, 
		// we know that the key was not in tree
		if(index == node.getNumItems() && node.getChild(0) == null) {
			this.printAllElements();
			throw new ElementNotFoundException("key is not in tree: " + key);
		}

		Object returnData;
		// if we are at an external node, simply remove data from node
		if(node.getChild(0) == null) {
			returnData = node.removeItem(index).element();
		// else, we are at an internal node, we must replace data with in-order
		// successor
		}else {
			TFNode successor = getInOrderSuccessor(node, index);
			returnData = node.replaceItem(index, successor.removeItem(0)).element();
			node = successor;
		}

		fixUnderflow(node);
		return returnData;
    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        myTree.insertElement(47, 47);
        myTree.insertElement(83, 83);
        myTree.insertElement(22, 22);
        myTree.insertElement(16, 16);
        myTree.insertElement(49, 49);
        myTree.insertElement(100, 100);
        myTree.insertElement(38, 38);
        myTree.insertElement(3, 3);
        myTree.insertElement(53, 53);
        myTree.insertElement(66, 66);
        myTree.insertElement(19, 19);
        myTree.insertElement(23, 23);
        myTree.insertElement(24, 24);
        myTree.insertElement(88, 88);
        myTree.insertElement(1, 1);
        myTree.insertElement(97, 97);
        myTree.insertElement(94, 94);
        myTree.insertElement(35, 35);
        myTree.insertElement(51, 51);

		//myTree.printAllElements();
		//System.out.println("removing\n");

		myTree.removeElement(19);
		myTree.removeElement(66);
		myTree.removeElement(100);
		myTree.removeElement(83);
		myTree.removeElement(51);
		myTree.removeElement(94);
		myTree.removeElement(49);
		myTree.removeElement(88);

		//myTree.printAllElements();
        System.out.println("test 1: simple test done");

		System.out.println();
        myTree = new TwoFourTree(myComp);
        int testSize = 10000;

		Random rng = new Random();
		Queue<Integer> nums = new LinkedList<Integer>();
        for (int i = 0; i < testSize; i++) {
			int j = rng.nextInt(testSize / 10);
			nums.add(j);
            myTree.insertElement(j, j);
//			if(i > testSize - 30) {
//				System.out.println("inserting " + j);
//				myTree.printAllElements();
//				myTree.checkTree();
//			}
        }
        System.out.println("removing");
        for (int i = testSize - 1; i >= 0; i--) {
			int j = nums.remove();
			if (i < 30){
				System.out.println("removing "+j);
			}
			//myTree.printAllElements();
            int out = (int)myTree.removeElement(j);
            if (out != j) {
                throw new TwoFourTreeException("main: wrong element removed: " + out +" ; " + j);
            }
			if (i < 30){
				myTree.printAllElements();
			}
			
        }
        System.out.println("test 2: random done");
		myTree.printAllElements();

		System.out.println();
        myTree = new TwoFourTree(myComp);
        testSize = 1000;
        for (int i = 0; i < testSize; i++) {
            myTree.insertElement(0, 0);
        }
        System.out.println("removing");
		int out;
        for (int i = testSize - 1; i >= 0; i--) {
            out = (int)myTree.removeElement(0);
            if (out != 0) {
                throw new TwoFourTreeException("main: wrong element removed: " + out);
            }
        }
        System.out.println("test 3: extreme duplicate test done");
		myTree.printAllElements();

		System.out.println();
        for (int i = 0; i < testSize; i++) {
            myTree.insertElement(i, i);
        }
        System.out.println("removing");
        for (int i = testSize - 1; i >= 0; i--) {
            out = (int)myTree.removeElement(i);
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed: " + out +" ; " + i);
            }
        }
        System.out.println("test 4: reverse sorted order remove done");
		myTree.printAllElements();

		System.out.println();
        for (int i = 0; i < testSize; i++) {
            myTree.insertElement(i, i);
        }
        System.out.println("removing");
        for (int i = 0; i < testSize; i++) {
            out = (int)myTree.removeElement(i);
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed: " + out +" ; " + i);
            }
        }
        System.out.println("test 5: sorted order remove done");
		myTree.printAllElements();

    }

    public void printAllElements() {
        int indent = 0;
        if (root() == null) {
            System.out.println("The tree is empty");
        }
        else {
            printTree(root(), indent);
        }
		System.out.println("");
    }

    public void printTree(TFNode start, int indent) {
        if (start == null) {
            return;
        }
        for (int i = 0; i < indent; i++) {
            System.out.print(" ");
        }
        printTFNode(start);
        indent += 4;
        int numChildren = start.getNumItems() + 1;
        for (int i = 0; i < numChildren; i++) {
            printTree(start.getChild(i), indent);
        }
    }

    public void printTFNode(TFNode node) {
        int numItems = node.getNumItems();
        for (int i = 0; i < numItems; i++) {
            System.out.print(((Item) node.getItem(i)).element() + " ");
        }
        System.out.println();
    }

    // checks if tree is properly hooked up, i.e., children point to parents
    public void checkTree() {
        checkTreeFromNode(treeRoot);
    }

    private void checkTreeFromNode(TFNode start) {
        if (start == null) {
            return;
        }

        if (start.getParent() != null) {
            TFNode parent = start.getParent();
            int childIndex = 0;
            for (childIndex = 0; childIndex <= parent.getNumItems(); childIndex++) {
                if (parent.getChild(childIndex) == start) {
                    break;
                }
            }
            // if child wasn't found, print problem
            if (childIndex > parent.getNumItems()) {
                System.out.println("Child to parent confusion");
                printTFNode(start);
            }
        }

        if (start.getChild(0) != null) {
            for (int childIndex = 0; childIndex <= start.getNumItems(); childIndex++) {
                if (start.getChild(childIndex) == null) {
                    System.out.println("Mixed null and non-null children");
                    printTFNode(start);
                }
                else {
                    if (start.getChild(childIndex).getParent() != start) {
                        System.out.println("Parent to child confusion");
                        printTFNode(start);
                    }
                    for (int i = childIndex - 1; i >= 0; i--) {
                        if (start.getChild(i) == start.getChild(childIndex)) {
                            System.out.println("Duplicate children of node");
                            printTFNode(start);
                        }
                    }
                }

            }
        }

        int numChildren = start.getNumItems() + 1;
        for (int childIndex = 0; childIndex < numChildren; childIndex++) {
            checkTreeFromNode(start.getChild(childIndex));
        }

    }
}
