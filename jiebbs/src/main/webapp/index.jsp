<%@ page contentType="text/html;charset=UTF-8" language="java"  pageEncoding="UTF-8"  %>
<html>
<body>
<h2>Hello World!

SpringMVC上传文件
<form id="from1" action="/jiebbs/manage/product/file_upload.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file" />
	<input type="submit" value="上传文件"/>
</form>

SpringMVC 富文本图片上传
<form id="from2" action="/jiebbs/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
	<input type="file" name="upload_file" />
	<input type="submit" value="上传文件">
</form>

</h2>
</body>
</html>
