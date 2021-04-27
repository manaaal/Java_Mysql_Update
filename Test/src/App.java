import java.sql.*;
import java.util.ArrayList;


public class App {

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) {
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0; /* both strings have zero length */
        }
        return (longerLength - getLevenshteinDistance(longer, shorter)) / (double) longerLength;
    }

    /**
     * LevenshteinDistance
     * copied from https://commons.apache.org/proper/commons-lang/javadocs/api-2.5/src-html/org/apache/commons/lang/StringUtils.html#line.6162
     */
    public static int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        if (n > m) {
            // swap the input strings to consume less memory
            String tmp = s;
            s = t;
            t = tmp;
            n = m;
            m = t.length();
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }

    public static void main(String[] args) {
        try
        {
            /**
             * Connecting to the Database from MySQL
             */
            String BDD = "PFATest3";
            String url = "jdbc:mysql://localhost:3306/" + BDD;
            String user = "root";
            String passwd = "";
            Connection conn = DriverManager.getConnection(url, user, passwd);

            /**
             * Setting up the Table Adresse
             */
            String query = "SELECT Adresse FROM patient;";
            Statement stm = conn.createStatement();
            ResultSet res = stm.executeQuery(query);

            /**
             *Storing values of adresses in an array
             */
            ArrayList<String> list= new ArrayList<String>();
            while(res.next()){
                list.add(res.getString("Adresse"));
            }
            String[] Adresse = new String[list.size()];
            Adresse = list.toArray(Adresse);

            /**
             * Testing the similarity function with a simple example "ALNIF" and "CENTRZ ALNIF"
             */
            /*
            System.out.println(similarity("ALNIF", "CENTRE ALNIF"));
             */



            /**
             * Updating the array of Addresses with changing the similar values to normalize the column
             */

            for(int i=0;i<= Adresse.length; i++)
            {
                System.out.println(String.format("i=%d",i));
                for(int j=i;j< Adresse.length;j++)
                {
                    if(similarity(Adresse[i],Adresse[j])>=0.4)
                    {
                        Adresse[j]=Adresse[i];
                    }
                    System.out.println(String.format("j=%d",j));
                }
            }

            /**
             * Printing the array of Adresses
             */

            for(int w=0; w< Adresse.length; w++)
            {
                System.out.println(Adresse[w]);
            }

            /**
             * Testing Updating the table with a simple example
             */
            /*
            String update = String.format("update patient set Adresse='%s' where num='%d';","CENTREALNIF",2);
            System.out.println(update);
            PreparedStatement preparedStmt = conn.prepareStatement(update);
            preparedStmt.executeUpdate();
             */

            
            /**
             * Updating the table patient with the array of addresses obtained from the code above
             */

            for (int i=0;i< Adresse.length;i++)
            {
                String update = String.format("update patient set Adresse='%s' where num='%d';",Adresse[i],i+1);
                System.out.println(update);
                PreparedStatement preparedStmt = conn.prepareStatement(update);
                preparedStmt.executeUpdate();
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
    }
}