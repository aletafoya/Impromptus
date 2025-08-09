const express = require("express"); // Importing express for creating the server
const db = require("./db"); // Importing the database connection module
const app = express(); // Creating an instance of express
const PORT = 8080;

// Single request for user when Logging in
app.get("/user", async (req, res) => {
    const username = req.query.username;

    if (!username) {
        return res.status(400).json({ error: "Username is required" });
    }

    try {
        // Simple query to fetch user by username
        const [rows] = await db.query("SELECT * FROM user WHERE username = ?", [username]);

        if (rows.length > 0) res.json(rows[0]);
        else res.json(null);

    } catch (err) {
        console.error(err);
        res.status(500).json({error: "Internal Server Error" });
    }
});

// Unique registration check for email and username
app.get("/uniqueRegistration", async (req, res) => {
    const email = req.query.email;
    const username = req.query.username;
    if (!email || !username) {
        return res.status(400).json({ error: "Parameters are required" });
    }

    try {
        // Another query to check if the email
        const [rows] = await db.query("SELECT * FROM user WHERE email = ? OR username = ?", [email, username]);
        
        if(rows.length > 0) res.json({ data : rows });
        else res.json(null);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: "Internal Server Error" });
    }
});

// Start the server
app.listen(PORT, () => {
    console.log(`Server is running on http://localhost:${PORT}`);
});
