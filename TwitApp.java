import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// twitUser class represents a user in the TwitApp
class TwitUser {
    String id; // user's ID
    List<TwitUser> followers; // list of followers
    List<TwitUser> followings; // list of followings
    List<String> newsFeed; // list of tweets in news feed
    DefaultListModel<String> newsFeedModel; // model for the news feed list
    long creationTime; // when the user was created
    long lastUpdateTime; // when the user was last updated

    // constructor for TwitUser class
    public TwitUser(String id) {
        this.id = id; // set the user's ID
        this.followers = new ArrayList<>(); // initialize followers list
        this.followings = new ArrayList<>(); // initialize followings list
        this.newsFeed = new ArrayList<>(); // initialize news feed list
        this.newsFeedModel = new DefaultListModel<>(); // initialize news feed model
        this.creationTime = System.currentTimeMillis(); // set creation time to current time
        this.lastUpdateTime = this.creationTime; // set last update time to creation time
    }

    // get user id
    public String getId() {
        return id;
    }

    // get followers of the user
    public List<TwitUser> getFollowers() {
        return followers;
    }

    // get followings of the user
    public List<TwitUser> getFollowings() {
        return followings;
    }

    // get news feed of the user
    public List<String> getNewsFeed() {
        return newsFeed;
    }

    // add a follower to the user's list
    public void addFollower(TwitUser follower) {
        followers.add(follower);
    }

    // add a following user to the user's list
    public void addFollowing(TwitUser following) {
        followings.add(following);
    }

    // post a tweet to the user's news feed and notify followers
    public void postTweet(String tweet) {
        long currentTime = System.currentTimeMillis(); // get current time
        newsFeed.add(tweet); // add tweet to news feed
        newsFeedModel.addElement(tweet); // add tweet to news feed model
        this.lastUpdateTime = currentTime; // update last update time
        for (TwitUser follower : followers) {
            follower.getNewsFeed().add(tweet); // add tweet to follower's news feed
            follower.newsFeedModel.addElement(tweet); // add tweet to follower's news feed model
            follower.lastUpdateTime = currentTime; // update follower's last update time
        }
    }

    @Override
    public String toString() {
        return id;
    }
}

// twitUserGroup class represents a group of users
class TwitUserGroup {
    String id; // group's ID
    List<Object> members; // list of members in the group
    long creationTime; // when the group was created

    // constructor for TwitUserGroup class
    public TwitUserGroup(String id) {
        this.id = id; // set the group's ID
        this.members = new ArrayList<>(); // initialize members list
        this.creationTime = System.currentTimeMillis(); // set creation time to current time
    }

    // get group id
    public String getId() {
        return id;
    }

    // get members of the group
    public List<Object> getMembers() {
        return members;
    }

    // add a member to the group
    public void addMember(Object member) {
        members.add(member);
    }

    @Override
    public String toString() {
        return id;
    }
}

// twitApp class is the main class for the TwitApp
public class TwitApp {
    private static TwitApp instance; // singleton instance
    private TwitUserGroup rootGroup; // root group
    private DefaultMutableTreeNode rootNode; // root node for the tree
    private DefaultTreeModel treeModel; // model for the tree

    // constructor for TwitApp class
    private TwitApp() {
        this.rootGroup = new TwitUserGroup("Root"); // initialize root group
        this.rootNode = new DefaultMutableTreeNode(rootGroup); // initialize root node
        this.treeModel = new DefaultTreeModel(rootNode); // initialize tree model
    }

    // singleton instance getter
    public static TwitApp getInstance() {
        if (instance == null) {
            instance = new TwitApp(); // create instance if it doesn't exist
        }
        return instance;
    }

    // add a user to a group and update the tree view
    public void addUser(TwitUser user, TwitUserGroup group) {
        group.addMember(user); // add user to the group
        DefaultMutableTreeNode groupNode = findNode(rootNode, group); // find the group's node
        if (groupNode != null) {
            groupNode.add(new DefaultMutableTreeNode(user)); // add user node to the group node
            treeModel.reload(); // reload the tree model
        }
    }

    // add a group to a parent group and update the tree view
    public void addGroup(TwitUserGroup group, TwitUserGroup parentGroup) {
        parentGroup.addMember(group); // add group to the parent group
        DefaultMutableTreeNode parentNode = findNode(rootNode, parentGroup); // find the parent group's node
        if (parentNode != null) {
            parentNode.add(new DefaultMutableTreeNode(group)); // add group node to the parent node
            treeModel.reload(); // reload the tree model
        }
    }

    // find a node in the tree by user/group object
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Object target) {
        if (root.getUserObject().equals(target)) {
            return root; // return the node if it matches the target
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            DefaultMutableTreeNode node = findNode(child, target);
            if (node != null) {
                return node; // return the found node
            }
        }
        return null; // return null if not found
    }

    // display the admin control panel
    public void displayAdminControlPanel() {
        JFrame frame = new JFrame("Admin Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create buttons and text fields
        JButton createUserButton = new JButton("Create User");
        JTextField userIdTextField = new JTextField(15);
        JButton createGroupButton = new JButton("Create Group");
        JTextField groupIdTextField = new JTextField(15);
        JButton showTotalUsersButton = new JButton("Total Users");
        JButton showTotalGroupsButton = new JButton("Total Groups");
        JButton showTotalTweetsButton = new JButton("Total Tweets");
        JButton showPositiveTweetsButton = new JButton("Positive Tweets");
        JButton openUserViewButton = new JButton("Open User View");
        JButton validateIDsButton = new JButton("Validate IDs");
        JButton lastUpdateUserButton = new JButton("Last Update User");

        JTree userTree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(userTree);

        // set layout and add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(new JLabel("Create User: "), constraints);
        constraints.gridx++;
        panel.add(userIdTextField, constraints);
        constraints.gridx++;
        panel.add(createUserButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(new JLabel("Create Group: "), constraints);
        constraints.gridx++;
        panel.add(groupIdTextField, constraints);
        constraints.gridx++;
        panel.add(createGroupButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(showTotalUsersButton, constraints);

        constraints.gridx++;
        panel.add(showTotalGroupsButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        panel.add(showTotalTweetsButton, constraints);

        constraints.gridx++;
        panel.add(showPositiveTweetsButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 3;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(treeScrollPane, constraints);

        // add new buttons to the layout
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel.add(openUserViewButton, constraints);

        constraints.gridx++;
        panel.add(validateIDsButton, constraints);

        constraints.gridx++;
        panel.add(lastUpdateUserButton, constraints);

        // add action listeners for buttons
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdTextField.getText().trim();
                if (!userId.isEmpty()) {
                    TwitUser newUser = new TwitUser(userId);
                    addUser(newUser, rootGroup);
                    userIdTextField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "User ID cannot be empty.");
                }
            }
        });

        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = groupIdTextField.getText().trim();
                if (!groupId.isEmpty()) {
                    TwitUserGroup newGroup = new TwitUserGroup(groupId);
                    addGroup(newGroup, rootGroup);
                    groupIdTextField.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "Group ID cannot be empty.");
                }
            }
        });

        showTotalUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalUsers = countTotalUsers(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Users: " + totalUsers);
            }
        });

        showTotalGroupsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalGroups = countTotalGroups(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Groups: " + totalGroups);
            }
        });

        showTotalTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalTweets = countTotalTweets(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Tweets: " + totalTweets);
            }
        });

        showPositiveTweetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int totalPositiveTweets = countTotalPositiveTweets(rootGroup);
                JOptionPane.showMessageDialog(frame, "Total Positive Tweets: " + totalPositiveTweets);
            }
        });

        openUserViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectedPath = userTree.getSelectionPath();
                if (selectedPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                    Object userObject = selectedNode.getUserObject();
                    if (userObject instanceof TwitUser) {
                        TwitUser selectedUser = (TwitUser) userObject;
                        displayUserView(selectedUser);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a user.");
                    }
                }
            }
        });

        validateIDsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean areIdsValid = validateIds(rootGroup);
                if (areIdsValid) {
                    JOptionPane.showMessageDialog(frame, "All IDs are unique.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Duplicate IDs found.");
                }
            }
        });

        lastUpdateUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String lastUpdateUserId = getLastUpdateUserId(rootGroup);
                if (lastUpdateUserId != null) {
                    JOptionPane.showMessageDialog(frame, "Last Update User ID: " + lastUpdateUserId);
                } else {
                    JOptionPane.showMessageDialog(frame, "No users found.");
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // display the user view
    private void displayUserView(TwitUser user) {
        JFrame frame = new JFrame("User View - " + user.getId());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        DefaultListModel<String> newsFeedModel = user.newsFeedModel;
        JList<String> newsFeedList = new JList<>(newsFeedModel);

        JTextField tweetTextField = new JTextField();
        JButton postTweetButton = new JButton("Post Tweet");

        JLabel creationTimeLabel = new JLabel("Creation Time: " + user.creationTime);
        JLabel lastUpdateTimeLabel = new JLabel("Last Update Time: " + user.lastUpdateTime);

        frame.add(new JScrollPane(newsFeedList), BorderLayout.CENTER);
        frame.add(tweetTextField, BorderLayout.SOUTH);
        frame.add(postTweetButton, BorderLayout.EAST);
        frame.add(creationTimeLabel, BorderLayout.NORTH);
        frame.add(lastUpdateTimeLabel, BorderLayout.WEST);

        postTweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tweet = tweetTextField.getText().trim();
                if (!tweet.isEmpty()) {
                    user.postTweet(tweet);
                    tweetTextField.setText("");
                    lastUpdateTimeLabel.setText("Last Update Time: " + user.lastUpdateTime);
                }
            }
        });

        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // count total users in the group
    private int countTotalUsers(TwitUserGroup group) {
        int count = 0;
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                count++;
            } else if (member instanceof TwitUserGroup) {
                count += countTotalUsers((TwitUserGroup) member);
            }
        }
        return count;
    }

    // count total groups in the group
    private int countTotalGroups(TwitUserGroup group) {
        int count = 1; // count the current group
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUserGroup) {
                count += countTotalGroups((TwitUserGroup) member);
            }
        }
        return count;
    }

    // count total tweets in the group
    private int countTotalTweets(TwitUserGroup group) {
        int count = 0;
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                count += ((TwitUser) member).getNewsFeed().size();
            } else if (member instanceof TwitUserGroup) {
                count += countTotalTweets((TwitUserGroup) member);
            }
        }
        return count;
    }

    // count total positive tweets in the group
    private int countTotalPositiveTweets(TwitUserGroup group) {
        int count = 0;
        Set<String> positiveWords = new HashSet<>();
        positiveWords.add("good");
        positiveWords.add("great");
        positiveWords.add("excellent");
        positiveWords.add("happy");
        positiveWords.add("positive");

        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                for (String tweet : ((TwitUser) member).getNewsFeed()) {
                    String[] words = tweet.split("\\s+");
                    for (String word : words) {
                        if (positiveWords.contains(word.toLowerCase())) {
                            count++;
                            break;
                        }
                    }
                }
            } else if (member instanceof TwitUserGroup) {
                count += countTotalPositiveTweets((TwitUserGroup) member);
            }
        }
        return count;
    }

    // validate IDs for uniqueness
    private boolean validateIds(TwitUserGroup group) {
        Set<String> ids = new HashSet<>();
        return collectIds(group, ids);
    }

    // collect IDs for uniqueness validation
    private boolean collectIds(TwitUserGroup group, Set<String> ids) {
        if (!ids.add(group.getId())) {
            return false; // duplicate ID found
        }
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                if (!ids.add(((TwitUser) member).getId())) {
                    return false; // duplicate ID found
                }
            } else if (member instanceof TwitUserGroup) {
                if (!collectIds((TwitUserGroup) member, ids)) {
                    return false; // duplicate ID found
                }
            }
        }
        return true;
    }

    // get the ID of the user with the most recent update
    private String getLastUpdateUserId(TwitUserGroup group) {
        long latestUpdateTime = 0;
        String lastUpdateUserId = null;

        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                TwitUser user = (TwitUser) member;
                if (user.lastUpdateTime > latestUpdateTime) {
                    latestUpdateTime = user.lastUpdateTime;
                    lastUpdateUserId = user.getId();
                }
            } else if (member instanceof TwitUserGroup) {
                String groupLastUpdateUserId = getLastUpdateUserId((TwitUserGroup) member);
                TwitUser groupLastUpdateUser = findUserById(rootGroup, groupLastUpdateUserId);
                if (groupLastUpdateUser != null && groupLastUpdateUser.lastUpdateTime > latestUpdateTime) {
                    latestUpdateTime = groupLastUpdateUser.lastUpdateTime;
                    lastUpdateUserId = groupLastUpdateUser.getId();
                }
            }
        }
        return lastUpdateUserId;
    }

    // find a user by ID in the group
    private TwitUser findUserById(TwitUserGroup group, String userId) {
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                TwitUser user = (TwitUser) member;
                if (user.getId().equals(userId)) {
                    return user;
                }
            } else if (member instanceof TwitUserGroup) {
                TwitUser user = findUserById((TwitUserGroup) member, userId);
                if (user != null) {
                    return user;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TwitApp.getInstance().displayAdminControlPanel();
            }
        });
    }
}

