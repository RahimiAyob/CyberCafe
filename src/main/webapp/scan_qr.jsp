<!DOCTYPE html>
<html>
<head>
    <title>Scan Desk QR Code</title>
    <script src="https://unpkg.com/html5-qrcode"></script>
    <style>
        #reader { width: 100%; max-width: 500px; margin: auto; }
    </style>
</head>
<body>
    <h2 style="text-align: center;">Scan the QR Code on Your Desk</h2>

    <div id="reader"></div>

    <script>
      function onScanSuccess(decodedText, decodedResult) {
        // decodedText contains the URL from the QR code
        // e.g., "http://localhost:8080/Cybercafe/QrHandler?seat=5"

        // stop scanning once we hit a match to prevent infinite loops
        html5QrcodeScanner.clear();

        // instantly redirect the browser to the URL decoded from the QR sticker
        window.location.href = decodedText;
      }

      function onScanFailure(error) {
        // handle scan failure silently, it fires constantly while looking for a code
      }

      // initialize the scanner grid
      let html5QrcodeScanner = new Html5QrcodeScanner(
        "reader", { fps: 10, qrbox: 250 }, false);
      html5QrcodeScanner.render(onScanSuccess, onScanFailure);
    </script>
</body>
</html>