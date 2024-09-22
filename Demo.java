import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Song {
    private int id;
    private String songName;
    private String movieName;
    private String musicName;
    private String singerName;
    private String composer;
    private String lyrist;
    private float size;
    private String filePath;

    // Getters and Setters
    public void setId(int n) {
        id = n;
    }

    public int getId() {
        return id;
    }

    public void setSongName(String n) {
        songName = n;
    }

    public String getSongName() {
        return songName;
    }

    public void setMovieName(String n) {
        movieName = n;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMusicName(String n) {
        musicName = n;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setSingerName(String n) {
        singerName = n;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setComposer(String n) {
        composer = n;
    }

    public String getComposer() {
        return composer;
    }

    public void setLyrist(String n) {
        lyrist = n;
    }

    public String getLyrist() {
        return lyrist;
    }

    public void setSize(float n) {
        size = n;
    }

    public float getSize() {
        return size;
    }

    public void setFilePath(String path) {
        filePath = path;
    }

    public String getFilePath() {
        return filePath;
    }
}

class MusicLibrary {
    private List<Song> songs = new ArrayList<>();
    private int currentId = 1; // To assign unique IDs to songs
    private Clip clip; // Audio clip
    private Thread playThread; // Thread for playing songs
    private boolean isPlaying = false; // To track if a song is playing
    private int currentSongId = -1; // To keep track of the currently playing song ID

    // Add a new song with filePath
    public void addSong(String songName, String movieName, String musicName, String singerName, String composer,
            String lyrist, float size, String filePath) {
        Song song = new Song();
        song.setId(currentId++);
        song.setSongName(songName);
        song.setMovieName(movieName);
        song.setMusicName(musicName);
        song.setSingerName(singerName);
        song.setComposer(composer);
        song.setLyrist(lyrist);
        song.setSize(size);
        song.setFilePath(filePath);
        songs.add(song);
        System.out.println("Song added: " + songName);
    }

    // Show all songs
    public void showAllSongs() {
        if (songs.isEmpty()) {
            System.out.println("No songs in the library.");
            return;
        }
        for (Song song : songs) {
            displaySongDetails(song);
        }
    }

    // Remove a song by ID
    public void removeSong(int id) {
        Song songToRemove = null;
        for (Song song : songs) {
            if (song.getId() == id) {
                songToRemove = song;
                break;
            }
        }
        if (songToRemove != null) {
            songs.remove(songToRemove);
            System.out.println("Song removed with ID: " + id);
        } else {
            System.out.println("Song with ID " + id + " not found.");
        }
    }

    // Update a song by ID
    public void updateSong(int id, String songName, String movieName, String musicName, String singerName,
            String composer, String lyrist, float size, String filePath) {
        for (Song song : songs) {
            if (song.getId() == id) {
                song.setSongName(songName);
                song.setMovieName(movieName);
                song.setMusicName(musicName);
                song.setSingerName(singerName);
                song.setComposer(composer);
                song.setLyrist(lyrist);
                song.setSize(size);
                song.setFilePath(filePath);
                System.out.println("Song updated with ID: " + id);
                return;
            }
        }
        System.out.println("Song with ID " + id + " not found.");
    }

    // Play a song by ID
    public void playSong(int id) {
        if (isPlaying) {
            stopSong(); // Stop the currently playing song if any
        }

        for (Song song : songs) {
            if (song.getId() == id) {
                final Song songToPlay = song;
                playThread = new Thread(() -> {
                    try {
                        File file = new File(songToPlay.getFilePath());
                        if (!file.exists()) {
                            System.out.println("File not found: " + songToPlay.getFilePath());
                            return;
                        }
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
                        clip = AudioSystem.getClip();
                        clip.open(audioStream);
                        System.out.println("Playing song: " + songToPlay.getSongName());
                        clip.start();
                        isPlaying = true;
                        currentSongId = id;

                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                isPlaying = false;
                                currentSongId = -1;
                                clip.close();
                            }
                        });
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                        System.out.println("Error playing the song: " + e.getMessage());
                    }
                });
                playThread.start();
                return;
            }
        }
        System.out.println("Song with ID " + id + " not found.");
    }

    // Stop the currently playing song
    public void stopSong() {
        if (isPlaying && clip != null) {
            clip.stop();
            clip.close();
            isPlaying = false;
            System.out.println("Song stopped.");
        } else {
            System.out.println("No song is currently playing.");
        }
    }

    // Change to a new song
    public void changeSong(int id) {
        stopSong(); // Stop the currently playing song if any
        playSong(id); // Start playing the new song
    }

    // Play a random song
    public void playRandomSong() {
        if (songs.isEmpty()) {
            System.out.println("No songs in the library.");
            return;
        }

        Random random = new Random();
        int randomSongId = songs.get(random.nextInt(songs.size())).getId();
        playSong(randomSongId);
    }

    // Helper method to display song details
    private void displaySongDetails(Song song) {
        System.out.println("Song ID: " + song.getId());
        System.out.println("Song Name: " + song.getSongName());
        System.out.println("Movie Name: " + song.getMovieName());
        System.out.println("Music Name: " + song.getMusicName());
        System.out.println("Singer Name: " + song.getSingerName());
        System.out.println("Composer: " + song.getComposer());
        System.out.println("Lyrist: " + song.getLyrist());
        System.out.println("Size: " + song.getSize() + " MB");
        System.out.println("---------------------------");
    }
}

public class Demo {
    public static void main(String[] args) {
        MusicLibrary library = new MusicLibrary();
        Scanner scanner = new Scanner(System.in);

        // Adding 5 songs by Arijit Singh with file paths
        library.addSong("Haareya", "Meri Pyaari Bindu", "Sachin-Jigar", "Arijit Singh", "Akshay Raj", "Priya Saraiya",
                3.03f, "11Haarerya.wav");

        library.addSong("Zinda", "Bhaag Milkha Bhaag", "Shankar-Ehsaan-Loy", "Siddharth Mahadevan", "Prasoon Joshi",
                "Swanand Kirkire", 4.35f, "Zinda.wav");

        library.addSong("Aayat", "Bajirao Mastani", "Sanjay Leela Bhansali", "Arijit Singh", "Nasir Faraaz",
                "Siddharth-Garima", 4.43f, "Aayat.wav");

        library.addSong("Tum Hi Ho", "Aashiqui 2", "Mithoon", "Arijit Singh", "Mithoon", "Mithoon", 4.22f,
                "TumHiHo.wav");

        library.addSong("Kabira", "Yeh Jawaani Hai Deewani", "Pritam", "Arijit Singh", "Amitabh Bhattacharya",
                "Pritam", 4.11f, "Kabira.wav");

        // Main loop
        while (true) {
            System.out.println("\nMusic Library Options:");
            System.out.println("1. Show all songs");
            System.out.println("2. Play a song");
            System.out.println("3. Stop the song");
            System.out.println("4. Change the song");
            System.out.println("5. Play a random song");
            System.out.println("6. Add a new song");
            System.out.println("7. Remove a song");
            System.out.println("8. Update a song");
            System.out.println("9. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline character

            switch (choice) {
                case 1:
                    library.showAllSongs();
                    break;
                case 2:
                    System.out.print("Enter the song ID to play: ");
                    int playId = scanner.nextInt();
                    library.playSong(playId);
                    break;
                case 3:
                    library.stopSong();
                    break;
                case 4:
                    System.out.print("Enter the new song ID to play: ");
                    int changeId = scanner.nextInt();
                    library.changeSong(changeId);
                    break;
                case 5:
                    library.playRandomSong();
                    break;
                case 6:
                    System.out.print("Enter song name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter movie name: ");
                    String movie = scanner.nextLine();
                    System.out.print("Enter music name: ");
                    String music = scanner.nextLine();
                    System.out.print("Enter singer name: ");
                    String singer = scanner.nextLine();
                    System.out.print("Enter composer: ");
                    String composer = scanner.nextLine();
                    System.out.print("Enter lyrist: ");
                    String lyrist = scanner.nextLine();
                    System.out.print("Enter size (in MB): ");
                    float size = scanner.nextFloat();
                    scanner.nextLine(); // Consume newline character
                    System.out.print("Enter file path: ");
                    String filePath = scanner.nextLine();
                    library.addSong(name, movie, music, singer, composer, lyrist, size, filePath);
                    break;
                case 7:
                    System.out.print("Enter the song ID to remove: ");
                    int removeId = scanner.nextInt();
                    library.removeSong(removeId);
                    break;
                case 8:
                    System.out.print("Enter the song ID to update: ");
                    int updateId = scanner.nextInt();
                    scanner.nextLine(); // Consume newline character
                    System.out.print("Enter new song name: ");
                    String newName = scanner.nextLine();
                    System.out.print("Enter new movie name: ");
                    String newMovie = scanner.nextLine();
                    System.out.print("Enter new music name: ");
                    String newMusic = scanner.nextLine();
                    System.out.print("Enter new singer name: ");
                    String newSinger = scanner.nextLine();
                    System.out.print("Enter new composer: ");
                    String newComposer = scanner.nextLine();
                    System.out.print("Enter new lyrist: ");
                    String newLyrist = scanner.nextLine();
                    System.out.print("Enter new size (in MB): ");
                    float newSize = scanner.nextFloat();
                    scanner.nextLine(); // Consume newline character
                    System.out.print("Enter new file path: ");
                    String newFilePath = scanner.nextLine();
                    library.updateSong(updateId, newName, newMovie, newMusic, newSinger, newComposer, newLyrist,
                            newSize, newFilePath);
                    break;
                case 9:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }
}
