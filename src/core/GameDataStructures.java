package core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Kumpulan struktur data yang benar-benar dipakai oleh gameplay.
 *
 * Materi yang diterapkan:
 * - Record/ADT untuk data pemain dan snapshot tutorial.
 * - Binary Tree untuk representasi bracket tournament.
 * - Queue untuk event pertandingan bot.
 * - LinkedList dipakai pada MultiplayerState untuk rotasi giliran.
 * - HashMap dipakai untuk indeks scoreboard berdasarkan ID pemain.
 * - Graph + BFS untuk jalur pergerakan keeper bot.
 */
public final class GameDataStructures {
    private GameDataStructures() {
    }

    /** ADT/Record data pemain scoreboard. */
    public record PlayerRecord(
            String id,
            String name,
            int score,
            int level,
            String status
    ) {
    }

    /** Record satu giliran multiplayer. */
    public record MultiplayerTurn(int shooterPlayer, int keeperPlayer) {
    }

    /** Snapshot yang disimpan pada Stack undo/redo Tutorial. */
    public record TutorialSnapshot(
            String phaseName,
            boolean tutorialComplete,
            String title,
            String hint
    ) {
    }

    public enum BotMatchStage {
        FOUR_TEAM_SEMIFINAL,
        EIGHT_TEAM_QUARTER_FINAL,
        EIGHT_TEAM_SEMIFINAL,
        FINAL
    }

    /** Data event pertandingan bot yang dimasukkan ke Queue. */
    public record BotMatchEvent(
            BotMatchStage stage,
            int destinationIndex,
            String teamA,
            String teamB
    ) {
    }

    /** Node Binary Tree untuk bracket tournament. */
    public static final class BracketNode {
        private final int labelIndex;
        private final String teamName;
        private final BracketNode left;
        private final BracketNode right;

        public BracketNode(
                int labelIndex,
                String teamName,
                BracketNode left,
                BracketNode right
        ) {
            this.labelIndex = labelIndex;
            this.teamName = teamName;
            this.left = left;
            this.right = right;
        }

        public int labelIndex() {
            return labelIndex;
        }

        public String teamName() {
            return teamName;
        }

        public BracketNode left() {
            return left;
        }

        public BracketNode right() {
            return right;
        }
    }

    /**
     * Binary Tree bracket. Root adalah juara, anak root adalah finalis,
     * dan daun adalah peserta awal.
     */
    public static final class TournamentBracketTree {
        private final BracketNode root;
        private final HashMap<Integer, BracketNode> nodesByLabel = new HashMap<>();

        private TournamentBracketTree(BracketNode root) {
            this.root = root;
            indexNodes(root);
        }

        public static TournamentBracketTree fourTeams(
                String[] participants,
                String[] semifinalWinners,
                String champion
        ) {
            BracketNode leaf0 = node(0, valueAt(participants, 0));
            BracketNode leaf1 = node(1, valueAt(participants, 1));
            BracketNode leaf2 = node(2, valueAt(participants, 2));
            BracketNode leaf3 = node(3, valueAt(participants, 3));

            BracketNode semifinal0 = new BracketNode(
                    4,
                    valueAt(semifinalWinners, 0),
                    leaf0,
                    leaf1
            );
            BracketNode semifinal1 = new BracketNode(
                    5,
                    valueAt(semifinalWinners, 1),
                    leaf2,
                    leaf3
            );
            BracketNode championNode = new BracketNode(
                    6,
                    safe(champion),
                    semifinal0,
                    semifinal1
            );
            return new TournamentBracketTree(championNode);
        }

        public static TournamentBracketTree eightTeams(
                String[] participants,
                String[] quarterWinners,
                String[] semifinalWinners,
                String champion
        ) {
            BracketNode[] leaves = new BracketNode[8];
            for (int i = 0; i < leaves.length; i++) {
                leaves[i] = node(i, valueAt(participants, i));
            }

            BracketNode quarter0 = new BracketNode(8, valueAt(quarterWinners, 0), leaves[0], leaves[1]);
            BracketNode quarter1 = new BracketNode(10, valueAt(quarterWinners, 1), leaves[2], leaves[3]);
            BracketNode quarter2 = new BracketNode(9, valueAt(quarterWinners, 2), leaves[4], leaves[5]);
            BracketNode quarter3 = new BracketNode(11, valueAt(quarterWinners, 3), leaves[6], leaves[7]);

            BracketNode semifinal0 = new BracketNode(
                    12,
                    valueAt(semifinalWinners, 0),
                    quarter0,
                    quarter1
            );
            BracketNode semifinal1 = new BracketNode(
                    13,
                    valueAt(semifinalWinners, 1),
                    quarter2,
                    quarter3
            );
            BracketNode championNode = new BracketNode(
                    14,
                    safe(champion),
                    semifinal0,
                    semifinal1
            );
            return new TournamentBracketTree(championNode);
        }

        public BracketNode root() {
            return root;
        }

        public BracketNode findByLabelIndex(int labelIndex) {
            return nodesByLabel.get(labelIndex);
        }

        /** Traversal pre-order menghasilkan ArrayList node untuk rendering UI. */
        public List<BracketNode> preOrder() {
            List<BracketNode> result = new ArrayList<>();
            preOrder(root, result);
            return result;
        }

        private void preOrder(BracketNode node, List<BracketNode> result) {
            if (node == null) {
                return;
            }
            result.add(node);
            preOrder(node.left(), result);
            preOrder(node.right(), result);
        }

        private void indexNodes(BracketNode node) {
            if (node == null) {
                return;
            }
            nodesByLabel.put(node.labelIndex(), node);
            indexNodes(node.left());
            indexNodes(node.right());
        }

        private static BracketNode node(int labelIndex, String teamName) {
            return new BracketNode(labelIndex, safe(teamName), null, null);
        }

        private static String valueAt(String[] values, int index) {
            if (values == null || index < 0 || index >= values.length) {
                return "";
            }
            return safe(values[index]);
        }

        private static String safe(String value) {
            return value == null ? "" : value;
        }
    }

    /** Node graph jalur keeper. */
    public enum KeeperPathNode {
        FAR_LEFT,
        LEFT,
        CENTER,
        RIGHT,
        FAR_RIGHT
    }

    /**
     * Graph jalur keeper. BFS dipakai untuk menemukan jalur terpendek
     * dari posisi keeper sekarang menuju zona target.
     */
    public static final class KeeperPathGraph {
        private final Map<KeeperPathNode, List<KeeperPathNode>> adjacency =
                new EnumMap<>(KeeperPathNode.class);

        public KeeperPathGraph() {
            for (KeeperPathNode node : KeeperPathNode.values()) {
                adjacency.put(node, new ArrayList<>());
            }
            connect(KeeperPathNode.FAR_LEFT, KeeperPathNode.LEFT);
            connect(KeeperPathNode.LEFT, KeeperPathNode.CENTER);
            connect(KeeperPathNode.CENTER, KeeperPathNode.RIGHT);
            connect(KeeperPathNode.RIGHT, KeeperPathNode.FAR_RIGHT);
        }

        public List<KeeperPathNode> shortestPath(KeeperPathNode start, KeeperPathNode target) {
            if (start == null || target == null) {
                return List.of(KeeperPathNode.CENTER);
            }
            if (start == target) {
                return List.of(start);
            }

            Queue<KeeperPathNode> bfsQueue = new ArrayDeque<>();
            Map<KeeperPathNode, KeeperPathNode> parent = new EnumMap<>(KeeperPathNode.class);
            bfsQueue.offer(start);
            parent.put(start, null);

            while (!bfsQueue.isEmpty()) {
                KeeperPathNode current = bfsQueue.poll();
                if (current == target) {
                    break;
                }
                for (KeeperPathNode neighbor : adjacency.getOrDefault(current, List.of())) {
                    if (!parent.containsKey(neighbor)) {
                        parent.put(neighbor, current);
                        bfsQueue.offer(neighbor);
                    }
                }
            }

            if (!parent.containsKey(target)) {
                return List.of(start, target);
            }

            ArrayList<KeeperPathNode> reversedPath = new ArrayList<>();
            KeeperPathNode cursor = target;
            while (cursor != null) {
                reversedPath.add(cursor);
                cursor = parent.get(cursor);
            }

            ArrayList<KeeperPathNode> path = new ArrayList<>();
            for (int i = reversedPath.size() - 1; i >= 0; i--) {
                path.add(reversedPath.get(i));
            }
            return path;
        }

        private void connect(KeeperPathNode first, KeeperPathNode second) {
            adjacency.get(first).add(second);
            adjacency.get(second).add(first);
        }
    }
}
