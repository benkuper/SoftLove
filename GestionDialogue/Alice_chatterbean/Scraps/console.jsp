<?xml version="1.0" encoding="ISO-8859-1"?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
  <head>
    <title>ProgramJ JSP Console</title>
    <link rel="stylesheet" type="text/css" href="style.css" />
  </head>
  <body>
    <form name="console" method="post" />

    <jsp:useBean id="bot" class="helio256.programj.BotBean" scope="session">
      <jsp:setProperty name="bot" property="root" value="/members/tDHsUdLSfD5egtnCfjdBiD6yRsyUblFL/" />
    </jsp:useBean>
    <jsp:setProperty name="bot" property="entry" />

    <p>
      <jsp:getProperty name="bot" property="response" />
    </p>

    <table border="0">
      <tr valign="top">
        <td>
          <textarea name="entry" rows="6" cols="25"></textarea>
        </td>
        <td rowspan="2">
          <textarea name="categories" rows="8" cols="80"><jsp:getProperty name="bot" property="categories" /></textarea>
        </td>
      </tr>
      <tr align="center" colspan="2">
        <td>
          <input value="Submit" type="submit" onClick="form.action = 'console.jsp'; form.submit" />
          <input value="Reset"  type="submit" onClick="form.action = 'reset.jsp';   form.submit" />
        </td>
      </tr>
    </table>
  </body>
</html>
