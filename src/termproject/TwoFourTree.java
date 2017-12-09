package termproject;

/**
 * Title:        Term Project 2-4 Trees
 * Description: An abstract data type for a 2-4 tree
 * Copyright:    Copyright (c) 2001
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

	private TFNode search(Object key) {
		TFNode current = treeRoot;
		TFNode parent = null;
		if(treeRoot == null) {
			throw new TwoFourTreeException("root was null");
		}

		// loop until we have reached the child of an external node, or until
		// we find the key
		while(current != null) {
			int index = FFGTE(current, key);
			if(treeComp.isEqual(current.getItem(index).key(), key)) {
				break;
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

	private int FFGTE(TFNode node, Object key) {
		int i;
		for(i = 0; i < node.getNumItems(); ++i) {
			if(treeComp.isGreaterThanOrEqualTo(node.getItem(i).key(), key)) {
				break;
			}
		}
		return i;
	}

	private int WCIT(TFNode node) {
		TFNode parent = node.getParent();
		int i;
		for(i = 0; i < parent.getNumItems() + 1; ++i) {
			if(parent.getChild(i) == node) {
				break;
			}
		}
		return i;
	}

	private TFNode getInOrderSuccessor(TFNode current, Object key) {
		if(current == null) {
			return null;
		}

		int index = FFGTE(current, key);
		TFNode next = getInOrderSuccessor(current.getChild(index), key);
		if(next == null) {
			return current;
		}else {
			return next;
		}
	}

	private void fixOverflow(TFNode node) {
		if(node.getNumItems() <= MAX_ITEMS) {
			return;
		}

		TFNode left = node.getChild(3);
		TFNode right = node.getChild(4);
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
		Item toSibling = node.deleteItem(3);
		Item toParent = node.deleteItem(2);
		node.setChild(3, null);
		node.setChild(4, null);

		// creates and populates new sibling node
		TFNode sibling = new TFNode();
		sibling.addItem(0, toSibling);
		sibling.setParent(parent);
		sibling.setChild(0, left);
		sibling.setChild(1, right);
		if(left != null) {
			left.setParent(sibling);
			right.setParent(sibling);
		}

		// inserts data into parent node and hooks up sibling
		parent.insertItem(index, toParent);
		parent.setChild(index + 1, sibling);
		
		// recursively call on parent
		fixOverflow(parent);
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
			
		TFNode node = getInOrderSuccessor(treeRoot, key);
		int index = FFGTE(node, key);
		node.insertItem(index, new Item(key, element));
		if(node.getNumItems() > MAX_ITEMS) {
			fixOverflow(node);
		}
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

		if(node.getItem(index).key() != key) {
			throw new ElementNotFoundException("element is not in tree");
		}

    }

    public static void main(String[] args) {
        Comparator myComp = new IntegerComparator();
        TwoFourTree myTree = new TwoFourTree(myComp);

        myTree.insertElement(47, 47);
		myTree.printAllElements();

        myTree.insertElement(83, 83);
		myTree.printAllElements();

        myTree.insertElement(22, 22);
		myTree.printAllElements();

        myTree.insertElement(16, 16);
		myTree.printAllElements();

        myTree.insertElement(49, 49);
		myTree.printAllElements();

        myTree.insertElement(100, 100);
		myTree.printAllElements();

        myTree.insertElement(38, 38);
		myTree.printAllElements();

        myTree.insertElement(3, 3);
		myTree.printAllElements();

        myTree.insertElement(53, 53);
		myTree.printAllElements();

        myTree.insertElement(66, 66);
		myTree.printAllElements();

        myTree.insertElement(19, 19);
		myTree.printAllElements();

        myTree.insertElement(23, 23);
		myTree.printAllElements();

        myTree.insertElement(24, 24);
		myTree.printAllElements();

        myTree.insertElement(88, 88);
		myTree.printAllElements();

        myTree.insertElement(1, 1);
		myTree.printAllElements();

        myTree.insertElement(97, 97);
		myTree.printAllElements();

        myTree.insertElement(94, 94);
		myTree.printAllElements();

        myTree.insertElement(35, 35);
		myTree.printAllElements();

        myTree.insertElement(51, 51);
		myTree.printAllElements();

        System.out.println("done");

        /*myTree = new TwoFourTree(myComp);
        final int TEST_SIZE = 10000;


        for (int i = 0; i < TEST_SIZE; i++) {
            myTree.insertElement(new Integer(i), new Integer(i));
                     myTree.printAllElements();
                     myTree.checkTree();
        }
        System.out.println("removing");
        for (int i = 0; i < TEST_SIZE; i++) {
            int out = (Integer) myTree.removeElement(new Integer(i));
            if (out != i) {
                throw new TwoFourTreeException("main: wrong element removed");
            }
            if (i > TEST_SIZE - 15) {
                myTree.printAllElements();
            }
        }
*/
        System.out.println("done");
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
