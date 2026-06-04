<!DOCTYPE html>
<html>
<head>
    <title>Scan Desk QR Code</title>
    <script src="assets/js/html5-qrcode.min.js"></script>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">
    <main class="theme-page theme-page--centered">
        <section class="theme-card theme-card--wide theme-scanner theme-center">
            <div>
                <span class="theme-badge theme-badge--teal">Entry Scanner</span>
                <h1 class="theme-title">Scan the QR Code on Your <strong>Desk</strong></h1>
                <p class="theme-subtitle">Point your camera at the desk QR code to continue into the correct session flow.</p>
            </div>

            <div id="reader"></div>
            <p class="theme-note">Camera permissions may take a moment to appear in your browser.</p>

            <hr class="theme-divider theme-mt-18">
            <p class="theme-center" style="margin: 0;">
                <a href="admin_login.jsp" class="theme-link-btn theme-link-btn--secondary" style="opacity: 0.7; font-size: 0.9rem;">Admin Access</a>
            </p>
        </section>
    </main>

    <script>
      function onScanSuccess(decodedText) {
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