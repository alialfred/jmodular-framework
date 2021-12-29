package com.alisoftclub.frameworks.modular.license;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.StringTokenizer;

public final class Version implements Serializable, Comparable<Version> {

    private static final long serialVersionUID = -3054349171116917643L;

    public static final char SEPARATOR = '.';
    private int major;
    private int minor;
    private int build;
    private String description;

    public static Version parse(String str) {
        Version result = new Version();
        result.parseString(str);
        return result;
    }

    public Version() {
    }

    private void parseString(String str) {
        this.major = 0;
        this.minor = 0;
        this.build = 0;
        this.description = "";
        StringTokenizer st = new StringTokenizer(str, ".", false);

        if (!st.hasMoreTokens()) {
            return;
        }
        String token = st.nextToken();
        try {
            this.major = Integer.parseInt(token, 10);
        } catch (NumberFormatException nfe) {
            return;
        }

        if (!st.hasMoreTokens()) {
            return;
        }
        token = st.nextToken();
        try {
            this.minor = Integer.parseInt(token, 10);
        } catch (NumberFormatException nfe) {
            return;
        }

        if (!st.hasMoreTokens()) {
            return;
        }
        token = st.nextToken();
        try {
            this.build = Integer.parseInt(token, 10);
        } catch (NumberFormatException numberFormatException) {
        }

        if (!st.hasMoreTokens()) {
            return;
        }
        token = st.nextToken();
        this.description = token;
    }

    public Version(int aMajor, int aMinor, int aBuild, String aDescription) {
        this.major = aMajor;
        this.minor = aMinor;
        this.build = aBuild;
        this.description = aDescription;
    }

    public int getMajor() {
        return this.major;
    }

    public int getMinor() {
        return this.minor;
    }

    public int getBuild() {
        return this.build;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGreaterOrEqualTo(Version other) {
        if (other == null) {
            return false;
        }
        if (this.major > other.major) {
            return true;
        }
        if (this.major == other.major && this.minor > other.minor) {
            return true;
        }
        if (this.major == other.major && this.minor == other.minor && this.build > other.build) {
            return true;
        }
        if (this.major == other.major && this.minor == other.minor && this.build == other.build) {
            return true;
        }

        return (this.major == other.major && this.minor == other.minor && this.build == other.build);
    }

    public boolean isCompatibleWith(Version other) {
        if (other == null) {
            return false;
        }
        if (this.major != other.major) {
            return false;
        }
        if (this.minor > other.minor) {
            return true;
        }
        if (this.minor < other.minor) {
            return false;
        }
        return (this.build >= other.build);
    }

    public boolean isEquivalentTo(Version other) {
        if (other == null) {
            return false;
        }
        if (this.major != other.major) {
            return false;
        }
        if (this.minor != other.minor) {
            return false;
        }
        return (this.build != other.build);
    }

    public boolean isGreaterThan(Version other) {
        if (other == null) {
            return false;
        }

        return (this.major > other.major || this.minor > other.minor || this.build > other.build);
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Version)) {
            return false;
        }
        Version other = (Version) obj;
        return (this.major == other.major && this.minor == other.minor && this.build == other.build);
    }

    @Override
    public String toString() {
        return "" + this.major + '.' + this.minor + '.' + this.build + '.' + this.description;
    }

    @Override
    public int compareTo(Version obj) {
        if (equals(obj)) {
            return 0;
        }
        if (this.major != obj.major) {
            return this.major - obj.major;
        }
        if (this.minor != obj.minor) {
            return this.minor - obj.minor;
        }
        if (this.build != obj.build) {
            return this.build - obj.build;
        }
//        if (this.micro != obj.micro) {
//            return this.micro - obj.micro;
//        }
        return this.major - obj.major - this.minor - obj.minor - this.build - obj.build;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeInt(this.major);
        out.writeInt(this.minor);
        out.writeInt(this.build);
        out.writeUTF(this.description);
    }

    private void readObject(ObjectInputStream in) throws IOException {
        this.major = in.readInt();
        this.minor = in.readInt();
        this.build = in.readInt();
        this.description = in.readUTF();
    }
}
