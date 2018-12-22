app.service("uploadFileService",function ($http) {
    //文件上传
    this.uploadFile=function() {
        var formData = new FormData();
        //<form  enctype=” multipart/form-data”>
        //<input name=”file” type=”file” />
        formData.append("file", file.files[0]);
        return $http({
            method: 'POST',
            url: "../upload/uploadFile.do",
            data: formData,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity
        });
    };
})