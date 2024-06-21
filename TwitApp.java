import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// twituser class represents a user in the twitapp
class TwitUser {
    String id;
    List<TwitUser> followers;
    List<TwitUser> followings;
    List<String> newsFeed;
    DefaultListModel<String> newsFeedModel;

    // constructor for twituser class
    public TwitUser(String id) {
        this.id = id;
        this.followers = new ArrayList<>();
        this.followings = new ArrayList<>();
        this.newsFeed = new ArrayList<>();
        this.newsFeedModel = new DefaultListModel<>();
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
        newsFeed.add(tweet);
        newsFeedModel.addElement(tweet);
        for (TwitUser follower : followers) {
            follower.getNewsFeed().add(tweet);
            follower.newsFeedModel.addElement(tweet);
        }
    }

    @Override
    public String toString() {
        return id;
    }
}

// twitusergroup class represents a group of users
class TwitUserGroup {
    String id;
    List<Object> members;

    // constructor for twitusergroup class
    public TwitUserGroup(String id) {
        this.id = id;
        this.members = new ArrayList<>();
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

// twitapp class is the main class for the twitapp
public class TwitApp {
    private static TwitApp instance;
    private TwitUserGroup rootGroup;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    // constructor for twitapp class
    private TwitApp() {
        this.rootGroup = new TwitUserGroup("Root");
        this.rootNode = new DefaultMutableTreeNode(rootGroup);
        this.treeModel = new DefaultTreeModel(rootNode);
    }

    // singleton instance getter
    public static TwitApp getInstance() {
        if (instance == null) {
            instance = new TwitApp();
        }
        return instance;
    }

    // add a user to a group and update the tree view
    public void addUser(TwitUser user, TwitUserGroup group) {
        group.addMember(user);
        DefaultMutableTreeNode groupNode = findNode(rootNode, group);
        if (groupNode != null) {
            groupNode.add(new DefaultMutableTreeNode(user));
            treeModel.reload();
        }
    }

    // add a group to a parent group and update the tree view
    public void addGroup(TwitUserGroup group, TwitUserGroup parentGroup) {
        parentGroup.addMember(group);
        DefaultMutableTreeNode parentNode = findNode(rootNode, parentGroup);
        if (parentNode != null) {
            parentNode.add(new DefaultMutableTreeNode(group));
            treeModel.reload();
        }
    }

    // find a node in the tree by user/group object
    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Object target) {
        if (root.getUserObject().equals(target)) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) root.getChildAt(i);
            DefaultMutableTreeNode node = findNode(child, target);
            if (node != null) {
                return node;
            }
        }
        return null;
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

        // add new button to the layout
        constraints.gridx = 0;
        constraints.gridy++;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.weighty = 0;
        panel.add(openUserViewButton, constraints);

        // event listeners for buttons
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userId = userIdTextField.getText().trim();
                if (!userId.isEmpty()) {
                    TwitUser newUser = new TwitUser(userId);
                    addUser(newUser, rootGroup); // adding to rootgroup (could be improved)
                    userIdTextField.setText("");
                }
            }
        });

        createGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String groupId = groupIdTextField.getText().trim();
                if (!groupId.isEmpty()) {
                    TwitUserGroup newGroup = new TwitUserGroup(groupId);
                    addGroup(newGroup, rootGroup); // adding to rootgroup (could be improved)
                    groupIdTextField.setText("");
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
                int totalTweets = countTotalTweets(rootGroup);
                int positiveTweets = countPositiveTweets(rootGroup);
                double percentage = (totalTweets == 0 ? 0 : (double) positiveTweets * 100.0 / totalTweets);
                JOptionPane.showMessageDialog(frame, "Positive Tweets: " + String.format("%.2f", percentage) + "%");
            }
        });

        openUserViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath selectedPath = userTree.getSelectionPath();
                if (selectedPath != null) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                    if (selectedNode.getUserObject() instanceof TwitUser) {
                        TwitUser user = (TwitUser) selectedNode.getUserObject();
                        displayUserView(user);
                    } else {
                        JOptionPane.showMessageDialog(frame, "Please select a user.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "No user selected.");
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    // count total users in a group
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

    // count total groups in a group
    private int countTotalGroups(TwitUserGroup group) {
        int count = 1; // count the group itself
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUserGroup) {
                count += countTotalGroups((TwitUserGroup) member);
            }
        }
        return count;
    }

    // count total tweets in a group
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

    // count positive tweets in a group
    private int countPositiveTweets(TwitUserGroup group) {
        int count = 0;
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                List<String> newsFeed = ((TwitUser) member).getNewsFeed();
                for (String tweet : newsFeed) {
                    if (tweet.contains("good") || tweet.contains("great") || tweet.contains("excellent")) {
                        count++;
                    }
                }
            } else if (member instanceof TwitUserGroup) {
                count += countPositiveTweets((TwitUserGroup) member);
            }
        }
        return count;
    }

    // find user by id
    private TwitUser findUserById(String id) {
        return findUserById(id, rootGroup);
    }

    // helper method to find user by id in a group
    private TwitUser findUserById(String id, TwitUserGroup group) {
        for (Object member : group.getMembers()) {
            if (member instanceof TwitUser) {
                if (((TwitUser) member).getId().equals(id)) {
                    return (TwitUser) member;
                }
            } else if (member instanceof TwitUserGroup) {
                TwitUser user = findUserById(id, (TwitUserGroup) member);
                if (user != null) {
                    return user;
                }
            }
        }
        return null;
    }

    // display the user view for a user
    public void displayUserView(TwitUser user) {
        JFrame frame = new JFrame("User View - " + user.getId());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // create components
        JLabel userIdLabel = new JLabel("User: " + user.getId());
        DefaultListModel<String> newsFeedModel = user.newsFeedModel;
        JList<String> newsFeedList = new JList<>(newsFeedModel);
        JScrollPane newsFeedScrollPane = new JScrollPane(newsFeedList);

        DefaultListModel<String> followingsModel = new DefaultListModel<>();
        for (TwitUser following : user.getFollowings()) {
            followingsModel.addElement(following.getId());
        }
        JList<String> followingsList = new JList<>(followingsModel);
        JScrollPane followingsScrollPane = new JScrollPane(followingsList);

        JTextField followUserTextField = new JTextField(20);
        JButton followUserButton = new JButton("Follow");

        JTextField tweetTextField = new JTextField(20);
        JButton postTweetButton = new JButton("Post Tweet");

        // set layout and add components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(5, 5, 5, 5);

        panel.add(userIdLabel, constraints);

        constraints.gridy++;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(new JLabel("News Feed:"), constraints);
        constraints.gridy++;
        panel.add(newsFeedScrollPane, constraints);

        constraints.gridy++;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        panel.add(new JLabel("Followings:"), constraints);
        constraints.gridy++;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        panel.add(followingsScrollPane, constraints);

        constraints.gridy++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        panel.add(followUserTextField, constraints);
        constraints.gridx++;
        panel.add(followUserButton, constraints);

        constraints.gridx = 0;
        constraints.gridy++;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        panel.add(tweetTextField, constraints);
        constraints.gridx++;
        panel.add(postTweetButton, constraints);

        // event listener for post tweet button
        postTweetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tweet = tweetTextField.getText().trim();
                if (!tweet.isEmpty()) {
                    user.postTweet(tweet);
                    tweetTextField.setText("");
                }
            }
        });

        // event listener for follow user button
        followUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String followUserId = followUserTextField.getText().trim();
                if (!followUserId.isEmpty()) {
                    TwitUser followUser = findUserById(followUserId);
                    if (followUser != null) {
                        user.addFollowing(followUser);
                        followUser.addFollower(user);
                        followingsModel.addElement(followUser.getId());
                        followUserTextField.setText("");
                    } else {
                        JOptionPane.showMessageDialog(frame, "User not found.");
                    }
                }
            }
        });

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    // main method to run the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TwitApp app = TwitApp.getInstance();
                app.displayAdminControlPanel();
            }
        });
    }
}
