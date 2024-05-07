const express = require('express');
const fs = require('fs');
const bodyParser = require('body-parser');
const app = express();
const port = 3000;

app.use(bodyParser.json({ limit: '50mb' }));

app.post('/saveSignature', (req, res) => {
    const data = req.body.image;
    const base64Data = data.replace(/^data:image\/png;base64,/, "");
    fs.writeFile('signature.png', base64Data, 'base64', err => {
        if (err) {
            console.error(err);
            return res.status(500).send({ message: 'Error saving the image' });
        }
        res.send({ message: 'Signature saved successfully' });
    });
});

app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
