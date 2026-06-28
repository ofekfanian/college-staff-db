package project4.ofekFanianAndTalOshri;

import java.io.*;

public class BinaryFile {
    private final String filePath;

    public BinaryFile(String filename) {
        this.filePath = System.getProperty("user.dir") + File.separator + filename;
    }

    public void saveCollege(College college) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(college);
        }
    }

    public College readCollege() throws IOException, ClassNotFoundException {
        File file = new File(filePath);
        if (!file.exists()) return null;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (College) in.readObject();
        }
    }
}
