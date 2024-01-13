
# Chuyên Đề Nguyên Cứu 1

Mining top-k weighted frequent itemsets from uncertain databases


## Cài đặt 

Cài đặt Java Development Kit (JDK) cho Windows nếu chưa có [Tại đây](https://www.oracle.com/java/technologies/javase/jdk15-archive-downloads.html)

Mở Command Prompt với quyền admin và đi tới thư mục chứa dự án

```bash
  cd [thư-mục-chứa-dự-án]
```

Clone dự án về bằng Command Prompt 

```bash
  git clone https://github.com/HuynhDucne/CD-nghien-cuu-Duc-va-Huyen.git
```

## Công nghệ sử dụng
- Ngôn ngữ: Java
- IDE: Intellij
- JDK: 15 java version "15.0.2"

## Cấu hình máy đang chạy
- Processor:	Intel(R) Core(TM) i5-1035G1 CPU @ 1.00GHz 1.19 GHz
- Installed: RAM	8.00 GB (7.78 GB usable)
- Edition:	Windows 11 Home Single Language

## Chạy ví dụ mẫu trong bài báo

Để chạy ví dụ mẫu trong bài báo, thực hiện các bước sau:

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/ca/pfv/spmf/tools/MemoryLogger.java ./src/algorithms/TUFP/GUI/MainTest_TUFP_Example.java  
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TUFP.GUI.MainTest_TUFP_Example  
```

> Có thể xem kết quả chạy chương trình đã có sẵn trong folder `src/output/output_example.txt`.

## Chạy các bộ tests

Ví dụ để chạy các bộ dataset như **_retail_**, thực hiện các 2 bước sau:

1. Tạo một file dataset mới có xác suất ngẫu nhiên cho mỗi item trong bộ dataset cần test

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/TUFP/GUI/DatasetFileFormat.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TUFP.GUI.DatasetFileFormat
```

> Sau khi chạy chương trình thành công, sẽ có một file retail_prob.txt có chứa xác suất ngẫu nhiên
> cho mỗi item được tạo ra trong thư mục `src/algorithms/TUFP/dataset_prob`.

> Trong folder `src/algorithms/TUFP/dataset` có chứa các bộ dataset có thể test.

> Trong trường hợp muốn chạy các bộ dataset khác thì cần vào file **DatasetFileFormat.java** 
> để điều chỉnh tên file chứa dataset (`filePath`) và tên file chứa dataset đã được định dạng 
> có xác suất ngẫu nhiên cho mỗi item (`filePathFormat`) tương ứng.

> _Trong trường hợp muốn chạy bộ dataset đã định dạng sẵn có xác suất ngẫu nhiên thì sẽ bỏ qua bước 1 này.
> Nhưng sẽ cần chỉnh lại tên file chứa dataset đã được định dạng có xác suất (`filePathFormat`) 
> trong hàm main của các file maintest (ví dụ: **MainTest_TUFP_Retail.java**) cho phù hợp trước khi thực hiện bước 2_.

2. Chạy giải thuật cho bộ dataset mới có xác suất ngẫu nhiên cho mỗi item

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/ca/pfv/spmf/tools/MemoryLogger.java ./src/algorithms/TUFP/GUI/MainTest_TUFP_Retail.java 
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TUFP.GUI.MainTest_TUFP_Retail
 
```

> Sau khi chạy chương trình thành công sẽ có một file **retail_prob.txt** 
> được tạo mới trong folder `src/algorithms/TUFP/dataset_prob`.
> File **retail_prob.txt** này chứa dataset được định dạng với các xác suất ngẫu nhiên.

> Kết quả chạy chương trình trên đã có sẵn trong folder `src/output/output_retail.txt`.

## Roadmap

- [x] Tìm hiểu bài báo, cài đặt được thuật toán trong bài

- [ ] Tìm kiếm thêm giải thuật để so sánh 

- [ ] Thêm vào thuộc tính weighted (thuộc tính của item)


## Authors

- Huỳnh Huỳnh Đức (52000197) - [@HuynhDucne](https://github.com/HuynhDucne)
- Tô Ngọc Huyền (52000217) - [@Huynneh](https://github.com/Huynneh)

