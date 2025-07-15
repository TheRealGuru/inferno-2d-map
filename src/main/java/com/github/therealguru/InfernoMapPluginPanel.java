package com.github.therealguru;

import com.google.inject.Inject;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.URI;

import static com.github.therealguru.Inferno2dMapPlugin.GRID_HEIGHT;
import static com.github.therealguru.Inferno2dMapPlugin.GRID_WIDTH;

public class InfernoMapPluginPanel extends PluginPanel {

    private Color[][] colorGrid = new Color[GRID_WIDTH][GRID_HEIGHT];
    private JLabel waveLabel;
    private MapCanvas mapCanvas;
    private InfernoMapConfig config;
    private Inferno2dMapPlugin plugin;

    public InfernoMapPluginPanel(Inferno2dMapPlugin plugin, InfernoMapConfig config) {
        super();
        this.plugin = plugin;
        this.config = config;
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        // Header with wave info
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        waveLabel = new JLabel("Current Wave: Unknown");
        waveLabel.setFont(waveLabel.getFont().deriveFont(Font.BOLD, 14f));
        headerPanel.add(waveLabel);

        // Key/Legend panel
        JPanel keyPanel = createKeyPanel();

        // Map and buttons container
        JPanel mapContainer = new JPanel(new BorderLayout());

        // Map canvas
        mapCanvas = new MapCanvas();
        mapCanvas.setPreferredSize(new Dimension(348, 360));

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        mapContainer.add(mapCanvas, BorderLayout.CENTER);
        mapContainer.add(buttonPanel, BorderLayout.SOUTH);

        // Layout
        add(headerPanel, BorderLayout.NORTH);
        add(keyPanel, BorderLayout.CENTER);
        add(mapContainer, BorderLayout.SOUTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton copyButton = new JButton("Copy Scout Tool URL");
        copyButton.addActionListener(e -> copyUrlToClipboard());
        copyButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton openButton = new JButton("Open Scout Tool URL");
        openButton.addActionListener(e -> openUrl());
        openButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(Box.createVerticalStrut(5)); // Small spacing at top
        buttonPanel.add(copyButton);
        buttonPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons
        buttonPanel.add(openButton);
        buttonPanel.add(Box.createVerticalStrut(5)); // Small spacing at bottom

        return buttonPanel;
    }

    private void copyUrlToClipboard() {
        try {
            String url = plugin.generateScoutUrl();
            StringSelection selection = new StringSelection(url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, null);

            // Optional: Show feedback
            JOptionPane.showMessageDialog(this, "URL copied to clipboard!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to copy URL: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openUrl() {
        try {
            String url = plugin.generateScoutUrl();
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback for systems that don't support Desktop
                JOptionPane.showMessageDialog(this,
                        "Cannot open browser. URL: " + url,
                        "Browser Error",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open URL: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createKeyPanel() {
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.Y_AXIS));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key:"));

        // Create key entries
        keyPanel.add(createKeyEntry(config.batColour(), "Bat"));
        keyPanel.add(createKeyEntry(config.blobColour(), "Blob"));
        keyPanel.add(createKeyEntry(config.meleeColour(), "Meleer"));
        keyPanel.add(createKeyEntry(config.rangeColour(), "Ranger"));
        keyPanel.add(createKeyEntry(config.mageColour(), "Mager"));
        keyPanel.add(createKeyEntry(config.pillarColour(), "Pillar"));

        return keyPanel;
    }

    private JPanel createKeyEntry(Color color, String label) {
        JPanel entry = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));

        // Colored box
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(15, 15));
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        // Label
        JLabel textLabel = new JLabel(label);

        entry.add(colorBox);
        entry.add(textLabel);

        return entry;
    }

    public void updateWave(int wave) {
        waveLabel.setText("Current Wave: " + wave);
    }

    public void updateGrid(Color[][] grid) {
        this.colorGrid = grid;
        mapCanvas.repaint();
    }

    private class MapCanvas extends JPanel {

        public MapCanvas() {
            super();
            addMouseMotionListener(new MouseMotionListener() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Not needed for tooltips
                }

                @Override
                public void mouseMoved(MouseEvent e) {
                    updateTooltip(e.getX(), e.getY());
                }
            });
        }

        private void updateTooltip(int mouseX, int mouseY) {
            // Calculate tile size and grid positioning (same logic as paintComponent)
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int tileWidth = panelWidth / GRID_WIDTH;
            int tileHeight = panelHeight / GRID_HEIGHT;
            int tileSize = Math.min(tileWidth, tileHeight);

            // Center the grid
            int startX = (panelWidth - (GRID_WIDTH * tileSize)) / 2;
            int startY = (panelHeight - (GRID_HEIGHT * tileSize)) / 2;

            // Check if mouse is within the grid bounds
            int gridEndX = startX + (GRID_WIDTH * tileSize);
            int gridEndY = startY + (GRID_HEIGHT * tileSize);

            if (mouseX >= startX && mouseX < gridEndX && mouseY >= startY && mouseY < gridEndY) {
                // Calculate which grid cell the mouse is over
                int gridX = (mouseX - startX) / tileSize;
                int gridY = (mouseY - startY) / tileSize;

                // Ensure we're within bounds (safety check)
                if (gridX >= 0 && gridX < GRID_WIDTH && gridY >= 0 && gridY < GRID_HEIGHT) {
                    // Note: gridY is already in display coordinates, but our colorGrid uses flipped Y
                    // The actual grid coordinates for tooltip should match the visual representation
                    int actualGridY = GRID_HEIGHT - 1 - gridY;
                    setToolTipText(String.format("X: %d, Y: %d", gridX, actualGridY));
                } else {
                    setToolTipText(null);
                }
            } else {
                // Mouse is outside the grid
                setToolTipText(null);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (colorGrid == null) return;

            Graphics2D g2d = (Graphics2D) g.create();

            // Calculate tile size based on panel size
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            int tileWidth = panelWidth / GRID_WIDTH;
            int tileHeight = panelHeight / GRID_HEIGHT;
            int tileSize = Math.min(tileWidth, tileHeight);

            // Center the grid
            int startX = (panelWidth - (GRID_WIDTH * tileSize)) / 2;
            int startY = (panelHeight - (GRID_HEIGHT * tileSize)) / 2;

            // Draw grid
            for (int x = 0; x < GRID_WIDTH; x++) {
                for (int y = 0; y < GRID_HEIGHT; y++) {
                    int gridY = GRID_HEIGHT - 1 - y;

                    Color tileColor = colorGrid[x][gridY];
                    if (tileColor != null) {
                        g2d.setColor(tileColor);
                    } else {
                        g2d.setColor(config.emptyColour());
                    }

                    int pixelX = startX + (x * tileSize);
                    int pixelY = startY + (y * tileSize);

                    g2d.fillRect(pixelX, pixelY, tileSize, tileSize);

                    // Draw grid lines
                    g2d.setColor(Color.GRAY);
                    g2d.drawRect(pixelX, pixelY, tileSize, tileSize);
                }
            }

            g2d.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(280, 290);
        }
    }
}