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
            xhr.open("POST", "/api/s3/upload"); // 서버 엔드포인트에 맞게 수정
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

    // 파일 다운로드 양식 처리
    const downloadForm = document.getElementById("download-form");

    downloadForm.addEventListener("submit", function (e) {
        e.preventDefault();

        // 선택한 폴더와 파일 이름 가져오기
        const folderSelect = document.getElementById("folder");
        const filenameInput = document.getElementById("filename");

        const folder = folderSelect.value;
        const filename = filenameInput.value;

        if (folder && filename) {
            // 파일 다운로드 링크 생성
            const downloadLink = document.createElement("a");
            downloadLink.href = `/api/s3/download/${folder}/${filename}`;
            downloadLink.download = filename;

            // 링크를 클릭하여 다운로드 시작
            downloadLink.click();
        } else {
            alert("폴더와 파일 이름을 모두 입력하세요.");
        }
        function listFiles() {
            const selectedFolder = document.getElementById("folder-select").value;
            fetch(`/api/s3/search/${selectedFolder}`)
                .then(response => response.json())
                .then(data => {
                    const fileListContainer = document.getElementById("file-list-container");

                    // 파일 목록 컨테이너 초기화
                    fileListContainer.innerHTML = "";

                    // 파일 목록 데이터를 컨테이너에 추가
                    data.forEach(file => {
                        const fileItem = document.createElement("div");
                        fileItem.textContent = file.key; // 파일 이름
                        fileListContainer.appendChild(fileItem);
                    });
                })
                .catch(error => {
                    console.error("파일 목록을 가져오는 동안 오류 발생:", error);
                });
        }

        // "조회" 버튼 클릭 시 파일 목록 조회 함수 호출
        const listFilesButton = document.getElementById("list-files-button");
        listFilesButton.addEventListener("click", listFiles);


        // 페이지 로드 시 파일 목록 조회를 수행하지 않도록 주석 처리
        // listFiles();
    });
});