document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("upload-form");
    const fileInput = document.getElementById("file-input");
    const statusDiv = document.getElementById("status");

    form.addEventListener("submit", function (e) {
        e.preventDefault();
        const file = fileInput.files[0];

        if (file) {
            // 파일 업로드를 위한 FormData 객체 생성
            const formData = new FormData();
            formData.append("file", file);

            // AJAX 요청 보내기
            const xhr = new XMLHttpRequest();
            xhr.open("POST", form.action); // 서버 엔드포인트에 맞게 수정
            xhr.onload = function () {
                if (xhr.status === 200) {
                    statusDiv.textContent = "파일 업로드 완료!";
                } else {
                    statusDiv.textContent = "파일 업로드 실패";
                }
            };

            // 파일 업로드 중에 진행 상황을 추적하려면 다음 라인을 추가하세요.
            // xhr.upload.addEventListener("progress", function(event) {
            //     const percent = (event.loaded / event.total) * 100;
            //     statusDiv.textContent = `파일 업로드 중... (${percent.toFixed(2)}%)`;
            // });

            xhr.send(formData);
        } else {
            statusDiv.textContent = "파일을 선택하세요.";
        }
    });

    // 파일 선택(input type="file") 이벤트 처리
    fileInput.addEventListener("change", function () {
        const selectedFile = fileInput.files[0];
        if (selectedFile) {
            // 선택한 파일 이름을 화면에 표시
            statusDiv.textContent = `선택한 파일: ${selectedFile.name}`;
        } else {
            statusDiv.textContent = "파일을 선택하세요.";
        }
    });
});
