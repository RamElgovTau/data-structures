/**
 * FibonacciHeap
 * <p>
 * An implementation of a Fibonacci Heap over integers.
 * <p>
 *
 * @author Ram Elgov
 * id: 206867517
 * username: ramelgov
 * @author id: 213231434
 * username: orenbachar
 */
public class FibonacciHeap {
    private HeapNode first;
    private HeapNode min;
    private int size = 0;
    private int countMarks = 0;
    private int countHeapTrees = 0;
    static int countCuts = 0, countLinks = 0;

    public HeapNode getMin() {
        return min;
    }

    public int getSize() {
        return size;
    }

    public int getCountMarks() {
        return countMarks;
    }

    public int getCountHeapTrees() {
        return countHeapTrees;
    }

    public static int getCountCuts() {
        return countCuts;
    }

    public static int getCountLinks() {
        return countLinks;
    }

    /**
     * Public boolean isEmpty()
     * <p>
     * Returns true if and only if the heap is empty.
     * complexity: O(1)
     */
    public boolean isEmpty() {
        return first == null;
    }

    /**
     * public HeapNode insert(int key)
     * <p>
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     * <p>
     * Returns the newly created node.
     * complexity: O(1)
     */
    public HeapNode insert(int key) {
        return insert(key, null);
    }

    /**
     * helper insert method for kMin.
     *
     * @param key  to insert.
     * @param info to insert with correspondingly given key.
     * @return Returns the newly created node.
     * The added key is assumed not to already belong to the heap.
     * <p>
     * Complexity O(1)
     */
    private HeapNode insert(int key, HeapNode info) {
        countHeapTrees += 1;
        size += 1;
        HeapNode heapNode = new HeapNode(key, info);
        if (isEmpty()) {
            // empty heap
            first = heapNode;
            first.next = first;
            first.prev = first;
            // maintain min
            min = heapNode;
        } else {
            // heap is not empty
            first.addSibling(heapNode);  // insert new heap node to the left of first
            first = heapNode;            // update first to point the new node inserted from left.
            // maintain min
            updateMin(heapNode);
        }
        return heapNode;
    }

    /**
     * public void deleteMin()
     * <p>
     * Deletes the node containing the minimum key.
     * complexity: W.C: O(n), amortized O(logn)
     */
    public void deleteMin() {
        // if the heap is not empty
        if (size > 1) {
            size -= 1;
            countHeapTrees -= 1;
            // make the orphan children roots themselves.
            HeapNode child = min.child;
            HeapNode tmp;
            if (child != null) {
                HeapNode leftMostChild = min.child;
                HeapNode rightMostChild = leftMostChild.prev;
                connectChildrenToRoots(leftMostChild, rightMostChild);
                transformChildrenToRoots(leftMostChild);
            } else {
                // min has no children
                min.next.prev = min.prev;
                min.prev.next = min.next;
            }
            if (first == min) {
                first = first.next;
            }
            consolidate(first);
        } else {
            min = null;
            first = null;
            size = 0;
            countHeapTrees = 0;
        }

    }

    private void connectChildrenToRoots(HeapNode leftMostChild, HeapNode rightMostChild) {
        // detach edges and attach them to the right
        leftMostChild.prev = min.prev;
        leftMostChild.prev.next = leftMostChild;
        rightMostChild.next = min.next;
        rightMostChild.next.prev = rightMostChild;

    }

    private void transformChildrenToRoots(HeapNode firstChild) {
        HeapNode tmp = firstChild;
        do {
            // make the current child a root.
            tmp.parent = null;
            countHeapTrees += 1;

            if (tmp.mark) {
                tmp.mark = false;
                countMarks -= 1;
            }
            tmp = tmp.next;
        } while (tmp != firstChild);
    }

    private void consolidate(HeapNode firsNode) {
        fromBuckets(toBuckets(firsNode));
    }

    private HeapNode[] toBuckets(HeapNode x) {
        HeapNode y;
        HeapNode[] buckets = new HeapNode[(int) Math.ceil(Math.log(size) / Math.log(2)) + 1];
        x.prev.next = null;
        while (x != null) {
            y = x;
            x = x.next;
            while (buckets[y.rank] != null) {
                y = link(y, buckets[y.rank]);
                countHeapTrees -= 1;
                buckets[y.rank - 1] = null;  // empty the "used" bucket
            }
            buckets[y.rank] = y;  // move the linked tree y to its new bucket at y.rank (the new rank after linking)
        }
        return buckets;
    }

    private void fromBuckets(HeapNode[] buckets) {
        countHeapTrees = 0;
        first = null;
        HeapNode curr = null;
        for (HeapNode tree : buckets) {
            if (tree != null) {
                countHeapTrees += 1;
                if (first != null) {
                    curr.next = tree;
                    tree.prev = curr;
                    curr = tree;
                    updateMin(tree);
                } else {
                    first = tree;
                    min = first;
                    curr = tree;
                }
            }
        }
        if (first != null) {
            first.prev = curr;
            curr.next = first;
        }
    }


    /**
     * public HeapNode findMin()
     * <p>
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     * complexity O(1)
     */
    public HeapNode findMin() {
        return min;// should be replaced by student code
    }

    /**
     * public void meld (FibonacciHeap heap2)
     * <p>
     * Melds heap2 with the current heap.
     * Preforms a "lazy" meld by concatenating heap2 to the right of this heap.
     * complexity: O(1)
     */
    public void meld(FibonacciHeap heap2) {
        if ((heap2 != null) && !heap2.isEmpty()) {
            size += heap2.size;
            countHeapTrees += heap2.countHeapTrees;
            countMarks += heap2.countMarks;
            HeapNode heap2Last = heap2.first.prev;
            heap2Last.next = first;
            first.prev.next = heap2.first;
            heap2.first.prev = first.prev;
            first.prev = heap2Last;
            updateMin(heap2.min);
        }
    }

    /**
     * public int size()
     * <p>
     * Returns the number of elements in the heap.
     * complexity: O(1)
     */
    public int size() {
        return size; // should be replaced by student code
    }

    private void updateMin(HeapNode node) {
        if (node.key < min.key) {
            min = node;
        }
    }

    /**
     * public int[] countersRep()
     * <p>
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     */
    public int[] countersRep() {
        int[] array;
        // heapOrder will hold the order of the tree with maximum order in the heap.
        if (!isEmpty()) {
            int heapOrder = heapOrder();
            // calculates the maximal order in the heap.
            array = new int[heapOrder + 1];
            fillCounters(array);
        } else {
            // an empty heap returns an empty array.
            array = new int[0];
        }
        return array; //	 to be replaced by student code
    }

    private int heapOrder() {
        int res = 0;
        HeapNode node = this.first;
        for (int i = 1; i <= countHeapTrees; i++) {
            res = Math.max(node.rank, res);
            node = node.next;
        }
        return res;
    }

    private void fillCounters(int[] array) {
        HeapNode node = first;
        array[node.rank] += 1;
        while (node.next != first) {
            node = node.next;
            array[node.rank] += 1;
        }
    }

    /**
     * public void delete(HeapNode x)
     * <p>
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     */
    public void delete(HeapNode x) {
        int delta = x.key - Integer.MIN_VALUE;
        decreaseKey(x, delta);
        deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     * <p>
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     * complexity: amortized O(1)
     */
    public void decreaseKey(HeapNode x, int delta) {
        x.key = x.key - delta;
        // check if new minNode
        updateMin(x);
//         invariant violation check
        if (x.parent != null && x.parent.key > x.key)
            cascadingCut(x, x.parent);
    }

    private void cut(HeapNode x, HeapNode y) {
        countCuts += 1;
        countHeapTrees += 1;  // cut adds a new root to the heap
        x.parent = null;
        if (x.mark) {
            x.mark = false;
            countMarks -= 1;
        }
        y.rank -= 1;
        if (x.next == x) {
            // node is the only sibling
            y.child = null;
        } else {
            x.prev.next = x.next;
            x.next.prev = x.prev;
            if (x == y.child)
                y.child = x.next;
        }
        first.addSibling(x);
    }

    /**
     * private void cascadingCut(HeapNode x, HeapNode y)
     * <p>
     *
     * @param x is the child to preform a cut on.
     * @param y is the parent whose child has removed as a result of the operation.
     *          <p>
     *          the implementation is based on the pseudo code given in slide 78.
     *          </p>
     *          complexity: amortized O(1). W.C O(logn).
     */
    private void cascadingCut(HeapNode x, HeapNode y) {
        cut(x, y);
        if (!y.isRoot()) {
            if (!y.mark) {
                y.mark = true;
                countMarks += 1;
            } else {
                cascadingCut(y, y.parent);
            }
        }
    }

    /**
     * public int potential()
     * <p>
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     * <p>
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential() {
        return countHeapTrees + 2 * countMarks;
    }

    /**
     * public static int totalLinks()
     * <p>
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks() {
        return countLinks; // should be replaced by student code
    }

    /**
     * Links two given heap nodes preserving heap invariant.
     *
     * @param x first node to link
     * @param y second node to link
     * @return the parent of the new linked tree
     */
    private HeapNode link(HeapNode x, HeapNode y) {
        HeapNode child, parent;
        if (x.key < y.key) {
            child = y;
            parent = x;
        } else {
            child = x;
            parent = y;
        }
        parent.addChild(child);
        countLinks += 1;
        return parent;
    }

    /**
     * public static int totalCuts()
     * <p>
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts() {
        return countCuts; // should be replaced by student code
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     * <p>
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     * <p>
     * ###CRITICAL### : you are NOT allowed to change H.
     * we are using a technic we saw at homework 4 that is based on creating a helper heap of size k.
     * each time the next min candidates will be inserted to the helper heap. finally, using findMin on the helper heap
     * we will be able to construct the kMin array.
     * complexity: O(k*deg(H)).
     */
    public static int[] kMin(FibonacciHeap H, int k) {
        int[] res = new int[k];
        FibonacciHeap help = new FibonacciHeap();
        HeapNode tmpMin, tmpMinFirstChild;
        if (H != null && H.min != null) {
            // initially insert the current minimum node.
            help.insert(H.min.key, H.min);
            for (int i = 0; i < k; i++) {
                tmpMin = help.findMin();
                res[i] = tmpMin.key;
                tmpMinFirstChild = tmpMin.info.child;
                if (tmpMinFirstChild != null)
                    addMinCandidates(help, tmpMinFirstChild);
                help.deleteMin();
            }
            return res; // should be replaced by student code
        }
        return new int[0];
    }

    private static void addMinCandidates(FibonacciHeap help, HeapNode first) {
        HeapNode cur = first;
        do {
            help.insert(cur.getKey(), cur);
            cur = cur.getNext();
        } while (cur != first);
    }

    public HeapNode getFirst() {
        return first;
    }

    /**
     * public class HeapNode
     * <p>
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     */
    public static class HeapNode {
        private int key;
        private int rank;
        private boolean mark;
        private HeapNode child, next, prev, parent, info;


        /**
         * creates a new HeapNode with the given key. initially the node is unmarked and has no children.
         *
         * @param key for the constructed node.
         */
        public HeapNode(int key) {
            this.key = key;     // key is the given key
            this.mark = false;  // unmarked
            this.rank = 0;      // no children
            // pointers will initialized to default (null)
        }

        public HeapNode(int key, HeapNode info) {
            this(key);
            this.info = info;
        }

        public int getKey() {
            return this.key;
        }

        public boolean isRoot() {
            return this.parent == null;
        }

        /**
         * Adds sibling to the left of this node.
         * <p>
         *
         * @param s the sibling to add
         *          complexity: O(1);
         * @pre s.mark == false
         * </p>
         */
        public void addSibling(HeapNode s) {
            s.prev = prev;
            prev.next = s;
            if (!isRoot()) {
                // this and s are not roots, update their parent's rank (i.e #children).
                parent.rank += 1;
            }
            s.next = this;
            prev = s;
            s.parent = parent;
        }

        /**
         * $pre: this node is not the only root in the heap
         * Removes this node from the heap using pointers.
         */
        public void remove() {
            if (prev == this) {
                // this is the only child of his parent
                if (!isRoot()) {
                    parent.child = null;
                    parent.rank = 0;
                }
            } else {
                // this node has sibling
                if (!isRoot()) {
                    if (parent.child == this) {
                        // update child pointer if needed
                        parent.child = next;
                    }
                    parent.rank -= 1;
                }
                //
                next.prev = prev;
                prev.next = next;
            }
        }

        /**
         * Adds a new child to this node as most left child.
         *
         * @param c this node's new child.
         */
        public void addChild(HeapNode c) {
            c.parent = this;
            if (child == null) {
                // this node has no children (i.e a leaf or rank 0)
                child = c;
                c.next = c;
                c.prev = c;
                rank += 1;
            } else {
                // this node has at least 1 child
                child.addSibling(c);
                child = c;  // maintain child to point to the left most child
            }
        }

        public int getRank() {
            return rank;
        }

        public boolean getMarked() {
            return mark;
        }

        public HeapNode getParent() {
            return parent;
        }

        public HeapNode getNext() {
            return next;
        }

        public HeapNode getPrev() {
            return prev;
        }

        public HeapNode getChild() {
            return child;
        }
    }
}
