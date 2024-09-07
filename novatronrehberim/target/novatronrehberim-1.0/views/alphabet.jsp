<%@page contentType="text/html" pageEncoding="UTF-8"%> 
<!doctype html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="Novatron">
    <link rel="icon" href="${pageContext.request.contextPath}/assets/images/icon.png">

    <title>Novatron Rehberim</title>

    <!-- Custom styles for this template -->
    <link href="${pageContext.request.contextPath}/resources/alphabet.css" rel="stylesheet">
  </head>
  <body>
    <header id="header-box">
      <h1 id="header-text" class="text">Novatron Rehberim</h1>
      <h1 id="header-page" class="text">Alfabe</h1>
      <a href="novatronrehberim/home"><img id="logo" src="${pageContext.request.contextPath}/assets/images/transparent_icon3.png" alt="Novatron"></a>
    </header>

    <div id="container">
      <div id="box">
        <div id="input-box">
          <form action="alphabet" method="POST">
          <p id="input-lbl" class="text">Harfinizi giriniz:</p>
          <input type="text" id="letters" name="letter" maxlength="1" required>
          <input type="hidden" id="userId" name="userId" value="${userId}"> <!-- delete if necessery-->>
          <div id="buttons">
            <button type="submit" id="create-story-btn" class="text">Öykü Yaz</button>
            <button type="button" id="clear-story-btn" class="text">Ekranı Sil</button>
          </div>
        </div>
      </form>
        <div id="display" readonly>
          <audio controls>
            <source src="${pageContext.request.contextPath}/resources/audio/output.mpeg?timestamp=${System.currentTimeMillis()}" type="audio/mpeg">
          </audio>

          <p id="story-display"><%= request.getAttribute("aiResponse") %></p>
        </div>
      </div>
    </div>

    <script>
      document.getElementById('clear-story-btn').addEventListener('click', function() {
        document.getElementById('story-display').innerText = '';
      });

      document.getElementById('letters').addEventListener('input', function() {
        let inputValue = this.value;
        if (/[^a-zA-Z]/.test(inputValue)) {
          this.value = '';
        } else {
          this.value = inputValue.toUpperCase();
        }
      });
    </script>
  </body>
</html>
