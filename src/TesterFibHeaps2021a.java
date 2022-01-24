//FibonacciHeap Tester

public class TesterFibHeaps2021a {

    public static int NUMBER_OF_TESTS = 16;
    public static int MAX_TESTS_SCORE = 10;  // "arbitrary units", not grade points.
    public static int TEST_EXCEPTION_VALUE = -888;
    public static int MAX_GRADE = 70; // actual grade points.

    public static int WATCHDOG_TIME_MS = 100; // number of milliseconds to sleep while waiting for test update.
    public static int MAX_WATCHDOGS = 20; // number of attempts before killing a test.

    public static void main(String[] args) {

        final int [] success = new int[NUMBER_OF_TESTS];

        final SimpleTests first_tester = new SimpleTests();
        final ComplexTests second_tester = new ComplexTests();

        for (int test_index = 0; test_index < NUMBER_OF_TESTS; test_index ++) {
            final int test_index_final = test_index;
            runWithInterrupt(success,
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                int result;
                                switch (test_index_final) {
                                    /*****************************************************/
                                    /*                 "UNIT TESTS"                      */
                                    /*****************************************************/
                                    case 0:  result = first_tester.testInit(); break;
                                    case 1:  result = first_tester.testEmpty(); break;
                                    case 2:  result = first_tester.testPotential(); break;
                                    case 3:  result = first_tester.testDecreaseKey(); break;
                                    case 4:  result = first_tester.testDelete(); break;
                                    case 5:  result = first_tester.testSize(); break;
                                    case 6:  result = first_tester.testInsert(); break;
                                    case 7:  result = first_tester.testDeleteMin(); break;
                                    case 8:  result = first_tester.testForestAndConsolidationLogicNonEmpties(); break;
                                    case 9:  result = first_tester.testForestAndConsolidationLogicWithEmpty(); break;
                                    case 10:  result = first_tester.testKMin(); break;
                                    case 11:  result = first_tester.testLinks(); break;
                                    case 12:  result = first_tester.testCuts(); break;

                                    /****************************************************/
                                    /*               "SCENARIO TESTS"                   */
                                    /****************************************************/
                                    case 13: result = second_tester.InsertInChunksThenDeleteMinInOrder(); break;
                                    case 14: result = second_tester.testIncDecIncInsertThenDeleteThenDeleteMins(); break;
                                    case 15: result = second_tester.AddingKeysInDifferentIntervalsThenDeleteByDeleteMinAndByFindMinDelete(); break;
                                    case 16: result = second_tester.testDecreaseKeyLoopingWithAndWithoutConsolidation(); break;
                                    default:
                                        System.out.println("Test-case " + test_index_final + " not accounted for!");
                                        throw new Exception("Test-case " + test_index_final + " not accounted for!");
                                }
                                success[test_index_final] = result;

                            } catch (Throwable e) {
                                success[test_index_final] = TEST_EXCEPTION_VALUE;
                            }
                        }
                    }), test_index_final);
        }
        printStatus(success , /* elaborate= */ true);
    }

    /** score_track[0] is the number of successes, score_track[1] is the total number of checks. */
    static void update_counters(boolean condition, int[] score_track) {
        score_track[1]++; // Always increase the number of checks.
        if (condition) score_track[0]++; // Increase success count only if condition holds.
    }

    private static void runWithInterrupt(int[] success, Thread thread, int idx) {
        thread.start();

        for (int i = 0; i < MAX_WATCHDOGS; i++) {
            try {
                Thread.sleep(WATCHDOG_TIME_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (!thread.isAlive())
                break;
        }
        if (thread.isAlive()) {
            thread.stop();
            success[idx] = TEST_EXCEPTION_VALUE;
        }
    }

    public static int printStatus(int[] scores, boolean elaborate) {
        int passed = 0;
        int exceptions = 0;
        int total_score_not_normalized = 0;
        String exceptionList = "";
        String passedList = "";
        String failedList = "";
        String failedListDetails = "";

        for (int i = 0; i < scores.length; i++) {
            int score = scores[i];
            total_score_not_normalized += (score == TEST_EXCEPTION_VALUE ? 0 : score);
            if (score == MAX_TESTS_SCORE) {
                passed++;
                passedList += " " + i;
            } else if (score == TEST_EXCEPTION_VALUE) {
                exceptions++;
                exceptionList += " " + i;
            } else { // partial success, refer to as 'failure'.
                failedList += " " + i;
                failedListDetails += " " + i + " (" + score + "/" + MAX_TESTS_SCORE + ")";
            }
        }

        System.out.println("============================================");
        System.out.println("==              Run Results:              ==");
        System.out.println("==  ==  ==  ==  ==  ==  ==  ==  ==  ==  ====");
        System.out.println("== Total Runs: " + scores.length);
        System.out.println("== Passed: " + passed);
        System.out.println("== Failed Runs: " + (scores.length - passed) + " (out of which are exceptions: " + exceptions + ")");
        //System.out.println("== Of which are exceptions: " + exceptions);
        if (elaborate) {
            System.out.println("== The following cases passed: " + passedList);
            System.out.println("== The following cases failed: " + failedList);
            System.out.println("== Their partial scores: " + failedListDetails);
            System.out.println("== Exceptions occurred in cases: " + exceptionList);

        }
        int max_score = MAX_TESTS_SCORE*scores.length;
        int actual_grade = (MAX_GRADE * total_score_not_normalized) / max_score;
        System.out.println("The final grade is: " + actual_grade + "/" + MAX_GRADE + " (in code-points: " + total_score_not_normalized + "/" + max_score + ").");
        return total_score_not_normalized;

    }

    static void fillInc(FibonacciHeap H, int start, int stop) {
        fillInOrder(H, start, stop - start + 1, 1);
    }
    static void fillDec(FibonacciHeap H, int start, int stop) {
        fillInOrder(H, stop, stop - start + 1, -1);
    }
    static void fillInOrder(FibonacciHeap H, int startValue, int countItems, int stepSize) {
        for (int i=0;i<countItems;i++) {
            H.insert(startValue + i*stepSize);
        }
    }

    static boolean test_depletion(FibonacciHeap H, int minValue, int nItems, int iterations, boolean checkEmpty) {
        for (int i=0; i < iterations; i++) {
            if (H.findMin().getKey() != minValue+i || H.size() != nItems-i) {
                // System.out.println("" + H.findMin().getKey() + ":" + (minValue+i) + ":" + H.size() + ":"+ (nItems-i));
                return false;
            }
            H.deleteMin();
        }
        if (checkEmpty) {
            return H.isEmpty();
        } else {
            return true;
        }
    }

    static boolean cmp_arrays(int[] arr1, int[] arr2) {
        if (arr1.length != arr2.length) return false;
        for (int i=0;i<arr1.length;i++)
            if (arr1[i] != arr2[i]) return false;
        return true;
    }

    /** Adds a new item and deletes it. Could insert an arbitrary item, but then key-uniqueness is not guaranteed. */
    static void triggerConsolidation(FibonacciHeap H) {
        if (H.isEmpty()) return;
        H.insert(H.findMin().getKey() - 1);
        H.deleteMin();
    }

    public static class SimpleTests {

        int testInit() {
            FibonacciHeap H = new FibonacciHeap();
            int[] counters = {0,0};

            update_counters(H.findMin() == null, counters);
            update_counters(H.size() == 0, counters);
            return (MAX_TESTS_SCORE*counters[0])/counters[1];
        }

        int testEmpty() {
            FibonacciHeap H = new FibonacciHeap();
            int[] counters = {0,0};

            update_counters(H.isEmpty(),counters);
            H.insert(1);
            update_counters(!H.isEmpty(),counters);
            H.insert(2);
            update_counters(!H.isEmpty(),counters);
            H.deleteMin();
            update_counters(!H.isEmpty(),counters);
            H.deleteMin();
            update_counters(H.isEmpty(),counters);
            return (MAX_TESTS_SCORE*counters[0])/counters[1];
        }

        int testInsert() {
            // Note: We didn't define 'H.getFirst()' or anything like that, so testing that cannot be part of our test.
            FibonacciHeap H = new FibonacciHeap();
            int[] counters = {0,0};
            H.insert(8);
            update_counters(H.findMin().getKey() == 8 , counters);
            H.insert(10);
            update_counters(H.findMin().getKey() == 8 , counters);
            update_counters(H.findMin().getKey() == 8 , counters); // Re-check that nothing has changed.
            H.insert(6);
            update_counters(H.findMin().getKey() == 6 , counters);
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testDeleteMin() {
            int[] counters = {0,0};

            int[][] expectedTrees = {  // reverse binary representation of: (empty),0,1,...,10.
                    {}, {}, {1}, {0,1}, {1,1},
                    {0,0,1}, {1,0,1}, {0,1,1}, {1,1,1},
                    {0,0,0,1}, {1,0,0,1}, {0,1,0,1},
            };

            for (int items=0; items<=11; items++) {
                FibonacciHeap H = new FibonacciHeap();

                for (int i=1; i <= items; i++) { H.insert(i); }
                H.deleteMin();
                if (items <= 1) {
                    update_counters(H.findMin() == null , counters);
                } else {
                    update_counters(H.findMin().getKey() == 2 , counters);
                }
                // Called twice to give more weight to this check:
                update_counters(cmp_arrays(H.countersRep(), expectedTrees[items]) , counters);
                update_counters(cmp_arrays(H.countersRep(), expectedTrees[items]) , counters);
            }
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testPotential() {
            FibonacciHeap H = new FibonacciHeap();
            int[] counters = {0,0};
            int size = 16;
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[size+1];

            boolean block_fine = true;
            for (int i=0;i<=size;i++) {
                nodes[i] = H.insert(i);
                if (H.potential() != i+1) block_fine = false;
            }
            update_counters(block_fine , counters);
            H.deleteMin();
            update_counters(H.potential() == 1, counters); // 1 tree
            H.delete(nodes[16]);
            update_counters(H.potential() == 3, counters); // 1 tree, 1 marked node (15).
            H.delete(nodes[15]);
            update_counters(H.potential() == 3, counters); // 1 tree, 1 marked node (13).
            H.delete(nodes[10]);
            update_counters(H.potential() == 5, counters); // 1 tree, 2 marked node (13,9).
            H.delete(nodes[14]);
            update_counters(H.potential() == 3, counters); // 3 trees, no marked nodes.
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testForestAndConsolidationLogicNonEmpties() {
            // choosing the values 10,22 and 10+22=32 also verifies indirectly that consolidation
            // is able to skip rank-0 trees when it collects the consolidated trees.
            // We don't verify the rank of trees as we do not want to explicitly test the order in which
            // they were collected. While it was defined to be in order for conformity, we don't want
            // to penalize students for this issue (e.g. if they reversed it by mistake).
            // However, we do check the numbers of ranks, using 'countersRep()'.
            int[] counters = {0,0};
            int size10 = 10;
            int size22 = 22;
            int[] ranksFor10singles = {10};
            int[] ranksFor22singles = {22};
            int[] ranksFor10 = {0,1,0,1};
            int[] ranksFor22 = {0,1,1,0,1};
            int[] ranksFor10and22 = {0,2,1,1,1};
            int[] ranksFor32 = {0,0,0,0,0,1};
            int[] ranksFor31 = {1,1,1,1,1};
            int[] ranksFor10consolidatedand22singles = {22,1,0,1};

            FibonacciHeap H,H2;

            // Non-empty melding.
            H = new FibonacciHeap();
            H2 = new FibonacciHeap();

            fillInOrder(H,1,size10,2);
            fillInOrder(H2,2,size22,2);

            update_counters(H.size() == size10 && cmp_arrays(H.countersRep(), ranksFor10singles), counters);
            update_counters(H2.size() == size22 && cmp_arrays(H2.countersRep(), ranksFor22singles), counters);

            triggerConsolidation(H);
            triggerConsolidation(H2);

            update_counters(H.size() == size10 && cmp_arrays(H.countersRep(), ranksFor10), counters);
            update_counters(H2.size() == size22 && cmp_arrays(H2.countersRep(), ranksFor22), counters);

            H.meld(H2);
            update_counters(H.size() == size10 + size22 && cmp_arrays(H.countersRep(), ranksFor10and22) , counters);

            triggerConsolidation(H);
            update_counters(H.size() == size10 + size22 && cmp_arrays(H.countersRep(), ranksFor32) , counters);

            H.deleteMin();
            update_counters(H.size() == size10 + size22 - 1 && cmp_arrays(H.countersRep(), ranksFor31) , counters);

            // Multiple melds.
            H = new FibonacciHeap();
            H2 = new FibonacciHeap();
            fillInOrder(H2,2,size10,2);
            triggerConsolidation(H2);
            H.meld(H2);
            H2 = new FibonacciHeap();
            fillInOrder(H2,100,size22,2);
            H.meld(H2);
            update_counters(H.size() == size10 + size22 && cmp_arrays(H.countersRep(), ranksFor10consolidatedand22singles) , counters);
            H.deleteMin();
            update_counters(H.size() == size10 + size22 -1 && cmp_arrays(H.countersRep(), ranksFor31) , counters);
            return (MAX_TESTS_SCORE*counters[0])/counters[1];
        }

        int testForestAndConsolidationLogicWithEmpty() {
            // A few more cases with respect to melding empty heaps.
            int[] counters = {0,0};
            int size10 = 10;
            int[] ranksFor10singles = {10};
            int[] ranksFor10 = {0,1,0,1};

            FibonacciHeap H,H2;

            // Empty meld: heap2.
            H = new FibonacciHeap();
            H2 = new FibonacciHeap();

            fillInOrder(H,1,size10,2);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep() , ranksFor10singles) , counters);
            triggerConsolidation(H);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep() , ranksFor10) , counters);

            H.meld(H2);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep(), ranksFor10), counters);
            triggerConsolidation(H);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep(), ranksFor10), counters);


            // Empty meld: this.
            H = new FibonacciHeap();
            H2 = new FibonacciHeap();

            fillInOrder(H2,1,size10,2);
            update_counters((H2.size() == size10) && cmp_arrays(H2.countersRep(), ranksFor10singles), counters);
            H.meld(H2);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep(), ranksFor10singles), counters);
            triggerConsolidation(H);
            update_counters((H.size() == size10) && cmp_arrays(H.countersRep(), ranksFor10), counters);

            return (MAX_TESTS_SCORE*counters[0])/counters[1];
        }

        int testDecreaseKey() {
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();
            int nItems = 16;
            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[nItems+1];
            for (int i=1;i<=nItems;i++) {
                nodes[i] = H.insert(i);
            }

            triggerConsolidation(H); // Single tree, nothing marked.
            update_counters(H.size() == nItems && H.potential() == 1, counters);

            H.decreaseKey(nodes[1], 100); // Nothing topological should happen since 1 is the root.
            update_counters(H.size() == nItems && H.potential() == 1, counters);

            H.decreaseKey(nodes[2], 100); // Nothing topological should happen since 2 is a child of 1.
            update_counters(H.size() == nItems && H.potential() == 1 , counters);

            H.decreaseKey(nodes[15], 1000); // Unlinks 15 from its parent 13, and marks 13.
            update_counters(H.size() == nItems && H.potential() == 4, counters);

            H.decreaseKey(nodes[10], 1000); // Unlinks 10 from its parent 9, and marks 10.
            update_counters(H.size() == nItems && H.potential() == 7, counters);

            H.decreaseKey(nodes[2], 1000); // Unlinks 2 from its parent 1, 1 is not marked (root).
            update_counters(H.size() == nItems && H.potential() == 8, counters);

            // Unlinks 14 from its parent 13, cascades to unlink 13 from 9 and 9 from 1. Unmarks 9,13 (1 is still a root).
            H.decreaseKey(nodes[14], 1000);
            update_counters(H.size() == nItems && H.potential() == 7, counters);

            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testDelete() {
            int[] counters = {0,0};

            {  // single node delete (equivalent to delete min).
                FibonacciHeap H = new FibonacciHeap();
                FibonacciHeap.HeapNode node = H.insert(1);
                update_counters(H.size() == 1 , counters);
                H.delete(node);
                update_counters(H.size() == 0 , counters);
            }
            {  // delete a few non-min, and a some effectively-min nodes.
                FibonacciHeap H = new FibonacciHeap();
                FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[5];
                for (int i=0;i<nodes.length;i++) {
                    nodes[i] = H.insert(i);
                }
                triggerConsolidation(H);
                update_counters(H.size() == 5, counters);
                H.delete(nodes[4]);
                update_counters(H.size() == 4 && H.findMin().getKey() == 0, counters);
                H.delete(nodes[0]);
                update_counters(H.size() == 3 && H.findMin().getKey() == 1, counters);
                H.delete(nodes[3]);
                update_counters(H.size() == 2 && H.findMin().getKey() == 1, counters);
                H.delete(nodes[1]);
                update_counters(H.size() == 1 && H.findMin().getKey() == 2, counters);
            }
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testSize() {
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();
            int n=10;
            update_counters(H.size() == 0 , counters);
            for (int i=1; i<=n;i++) {
                H.insert(i);
                update_counters(H.size() == i , counters);
            }
            for (int i=n-1; i>=0;i--) {
                H.deleteMin();
                update_counters(H.size() == i , counters);
            }
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testKMin() {
            // Because we formally defined the function to work only when 0 < k < size(H),
            // We do not test for k=size(H) or k=0, which also implicitly exclude size(H)<2.
            FibonacciHeap H;
            int[] counters = {0,0};

            int[][] test_cases = {
                    // {}, {1},  // size(H) >= 2. size(H)=0,1 is not formally defined.
                    {1,2,3,4}, // we get a heap 1{2 , 3{4}}. For k>2, this also verifies that we don't query nulls (children of '2') in our scan.
                    {15,1,4,7,13,12,16,3,6,2,5,8,9,10,11,14}, // some out-of-order heap.
                    // In the future we can add cases for heap sizes that are not a power of 2.
            };

            for (int c=0;c<test_cases.length; c++) {
                H = new FibonacciHeap();
                boolean is_case_good = true; // Unless determined otherwise.
                for (int i=0;i<test_cases[c].length; i++) {
                    H.insert(test_cases[c][i]);
                }
                triggerConsolidation(H);

                // verify the output for all possible values of k up to the size of the heap, excluding k=0 and k=size(H).
                for (int k=1; k<=test_cases[c].length - 1; k++) {
                    int[] mins = FibonacciHeap.kMin(H, k);
                    is_case_good &= (mins.length == k);
                    for (int i=0; i<k; i++) {
                        is_case_good &= (mins[i] == i+1);
                    }
                }
                update_counters(is_case_good, counters);
            }
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testLinks() {
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();

            // We can't reset the counter, so save the initial values for base-line.
            int links_baseline = FibonacciHeap.totalLinks();

            H.insert(-100);
            update_counters(FibonacciHeap.totalLinks() - links_baseline == 0, counters);

            H.insert(-99);
            triggerConsolidation(H);
            update_counters(FibonacciHeap.totalLinks() - links_baseline == 1, counters);

            for (int i=0;i<1022; i++) {
                H.insert(i);
            }

            update_counters(FibonacciHeap.totalLinks() - links_baseline == 1, counters);

            triggerConsolidation(H);
            update_counters(FibonacciHeap.totalLinks() - links_baseline == 1023, counters);

            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testCuts() {
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();

            // We can't reset the counter, so save the initial values for base-line.
            int cuts_baseline = FibonacciHeap.totalCuts();

            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[16];
            for (int i=0;i<nodes.length;i++) {
                nodes[i] = H.insert(i);
            }

            triggerConsolidation(H);
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 0, counters);

            for (int i=0;i<8; i++) {
                H.deleteMin(); // Should not increase the number of cuts.
            }
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 0, counters);

            for (int i=8;i<12; i++) {
                H.delete(nodes[i]);  // This is the minimum now, should not add cuts.
            }
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 0, counters);

            // Now the heap is 12{13, 14{15}}. We delete from the leaves, adding 3 cuts in total.
            H.delete(nodes[15]);
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 1, counters);
            H.delete(nodes[14]);
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 2, counters);
            H.delete(nodes[13]);
            update_counters(FibonacciHeap.totalCuts() - cuts_baseline == 3, counters);

            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

    }
    public static class ComplexTests {

        int InsertInChunksThenDeleteMinInOrder() {
			/* This test does:
			1) Inserts a block of increasing values (B1). Then a block of larger values in decreasing order (B2).
			2) Then it inserts a block of intermediate values (B3, such that B1 < B3 < B2).
			3) Then it deletes B1 via 'deleteMin'.
            4) Then it inserts three additional blocks B4,B5,B6 in a similar manner (B5 is reversed), such that B2 < B4 < B5 < B6.
            5) Then it depletes the heap using 'deleteMin'.
			*/
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();

            fillInc(H, 0,999);
            fillDec(H, 4000,4999);
            fillInc(H, 2000,2999);
            update_counters(test_depletion(H, 0, 3000, 1000, false) , counters);

            fillInc(H, 6000,6999);
            fillDec(H, 8000,8999);
            fillInc(H, 10000,10999);

            update_counters(test_depletion(H, 2000, 5000, 1000, false) , counters);
            update_counters(test_depletion(H, 4000, 4000, 1000, false) , counters);
            update_counters(test_depletion(H, 6000, 3000, 1000, false) , counters);
            update_counters(test_depletion(H, 8000, 2000, 1000, false) , counters);
            update_counters(test_depletion(H, 10000, 1000, 1000, true) , counters);
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testIncDecIncInsertThenDeleteThenDeleteMins() {
			/* This test does:
			1) Inserts a block of increasing values (B1). Then a block of larger values in decreasing order (B2).
			2) Then it inserts a block of intermediate values B3, such that B1 < B3 < B2.
			3) Then the test calls 'delete' on half of the values of B3.
			4) Finally, the test depletes the heap using 'deleteMin'.
			*/
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();

            fillInc(H, 1000,1999);
            fillDec(H, 3000,3999);

            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[1000];

            for (int i = 2000; i < 3000; i++) {
                nodes[i-2000] = H.insert(i);
            }

            for (int i = 0; i < 500; i++) {
                update_counters(H.findMin().getKey() == 1000 && H.size() == 3000-i, counters);
                H.delete(nodes[i]);
            }

            update_counters(test_depletion(H, 1000, 2500, 1000, false), counters);
            update_counters(test_depletion(H, 2500, 1500, 1500, true) , counters);

            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int AddingKeysInDifferentIntervalsThenDeleteByDeleteMinAndByFindMinDelete() {
			/* This test does:
			1) Inserts a block of increasing values (B1). Then a block of decreasing values B2, B1 < B2.
			2) It then depletes the heap using 'deleteMin' on even steps and 'findMin'+'delete' on odd steps. Check-points by the end of each block.
            3) Finally, it checks the validity of isEmpty.
			*/
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();
            fillInOrder(H, 1000,1000,1); // increasing
            fillInOrder(H, 7999,1000,-1);  // reversed

            boolean isBlockOk = true;
            for (int i = 1000; i < 2000; i++) {
                isBlockOk &= (i == H.findMin().getKey());
                if (i%2 == 0) {
                    H.deleteMin();
                } else {
                    H.delete(H.findMin());
                }
            }
            update_counters(isBlockOk, counters);

            isBlockOk = true;
            for (int i = 7000; i < 8000; i++) {
                isBlockOk &= (i == H.findMin().getKey());
                if (i%2 == 0) {
                    H.deleteMin();
                } else {
                    H.delete(H.findMin());
                }
            }
            update_counters(isBlockOk, counters);
            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }

        int testDecreaseKeyLoopingWithAndWithoutConsolidation() {
			/* This test does:
			1) Inserts a block of values.
			2) Then cycle over each of the values, and decreaseKey on it to be the new minimum.
			3) Throughout step-2, validate that nothing change in the structure of the heap.
			4) Then repeat step 1, this time consolidate B1. Then repeat step-2, and check that in the end all items are singletons. 
			*/
            int[] counters = {0,0};
            FibonacciHeap H = new FibonacciHeap();
            boolean isBlockOk;

            int[] forest = {1025};
            int blockSize = forest[0];
            int initial_trees_count = 2; // number of 1-bits in the binary representation of 'blockSize'. 
            int valueOffset = 1234;
            int decreaseSize = 99999;

            FibonacciHeap.HeapNode[] nodes = new FibonacciHeap.HeapNode[blockSize];
            for (int i=0;i<blockSize;i++) {
                nodes[i] = H.insert(valueOffset + i);
            }

            // Phase 1: decreaseKey without any deletions. No consolidation happens, we have a singletons-forest.
            isBlockOk = true;
            for (int i=0;i<blockSize;i++) {
                H.decreaseKey(nodes[blockSize-1-i], decreaseSize); // reversed order, always update the maximum key.
                isBlockOk &= (H.size() == blockSize);
                isBlockOk &= (cmp_arrays(H.countersRep(),forest));
            }
            update_counters(isBlockOk, counters);

            // Phase 2: Similar to phase 1, but we begin with a consolidation.
            // For this reason, the tree breaks-down slowly, we don't track it exactly, only upper-bound the potential.
            triggerConsolidation(H);
            isBlockOk = true;
            for (int i=0;i<blockSize;i++) {
                H.decreaseKey(nodes[blockSize-1-i], decreaseSize); // reversed order, always update the maximum key.
                isBlockOk &= (H.size() == blockSize);
                isBlockOk &= (H.potential() <= initial_trees_count + 3*(i+1)); // Each step cannot more than mark a node and add a tree.
            }
            isBlockOk &= (cmp_arrays(H.countersRep(),forest)); // Singletons-forest.
            isBlockOk &= (H.potential() == H.size());  // No node should be marked at this point.
            update_counters(isBlockOk, counters);

            return (MAX_TESTS_SCORE * counters[0]) / counters[1];
        }
    }
}