
# Chuyên Đề Nguyên Cứu 1

Mining top-k weighted frequent itemsets from uncertain databases

## Nội dung
- [Ban đầu] Giải thuật Top-k Uncertain Frequent Pattern (TUFP) - trong folder `TUFP`
- [Cải biên] Giải thuật Top-k Weighted Uncertain Frequent Pattent (TWUFP) - trong folder `TWUFP`
- [So sánh] Giải thuật Weighted Uncertain Interesting Patterns (WUIP) trong folder `WUIP` - ĐANG THỰC HIỆN ...

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

## Chạy ví dụ mẫu trong bài báo với giải thuật TUFP

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

> Có thể xem kết quả chạy chương trình đã có sẵn trong folder `src/algorithms/TUFP/output/output_TUFP_example.txt`.

## Chạy các bộ tests với giải thuật TUFP

Ví dụ để chạy giải thuật TWUFP cho bộ dataset **_retail_**, thực hiện các 2 bước sau:

1. Tạo một file dataset mới có xác suất ngẫu nhiên cho mỗi item trong bộ dataset cần test

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/TUFP/GUI/DatasetGUI.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TUFP.GUI.DatasetGUI
```

> Sau khi chạy chương trình thành công, sẽ có một file retail_prob.txt có chứa xác suất ngẫu nhiên
> cho mỗi item được tạo ra trong thư mục `src/algorithms/TUFP/dataset_prob`.

> Trong folder `src/algorithms/TUFP/dataset` có chứa các bộ dataset có thể test.

> Trong trường hợp muốn chạy các bộ dataset khác thì cần vào file **DatasetGUI.java** 
> để điều chỉnh tên file chứa dataset (`fileData`) và tên file chứa dataset đã được định dạng 
> có xác suất ngẫu nhiên cho mỗi item (`fileProb`) tương ứng.

> _Trong trường hợp muốn chạy bộ dataset đã định dạng sẵn có xác suất ngẫu nhiên thì sẽ bỏ qua bước 1 này.
> Nhưng sẽ cần chỉnh lại tên file chứa dataset đã được định dạng có xác suất (`fileProb`) 
> trong hàm main của các file maintest (ví dụ: **MainTest_TUFP_Retail.java**) cho phù hợp trước khi thực hiện bước 2_.

2. Chạy giải thuật TUFP cho bộ dataset mới có xác suất ngẫu nhiên cho mỗi item

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

> Kết quả chạy chương trình trên đã có sẵn trong folder `src/algorithms/TUFP/output/output_TUFP_retail.txt`.

## Chạy các bộ tests với giải thuật TWUFP

Ví dụ để chạy giải thuật TWUFP cho bộ dataset **_retail_**, thực hiện các 2 bước sau:

1. Tạo một file chứa trọng số (weight) của các item và một file dataset mới có xác suất ngẫu nhiên cho mỗi item trong bộ dataset cần test

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/TWUFP/GUI/DatasetGUI.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TWUFP.GUI.DatasetGUI
```

> Sau khi chạy chương trình thành công, 
>- Tạo ra file **retail_weight.txt** có chứa trọng số ngẫu nhiên cho mỗi item.
> File được lưu trong thư mục `src/algorithms/TWUFP/weight`.
>- Tạo ra  file **retail_prob.txt** có chứa xác suất ngẫu nhiên cho mỗi item.
> File được lưu trong thư mục `src/algorithms/TWUFP/dataset_prob`.

> Trong folder `src/algorithms/TUFP/dataset` có chứa các bộ dataset có thể test.

> Trong trường hợp muốn chạy các bộ dataset khác thì cần vào file **DatasetGUI.java**
> để điều chỉnh tên file chứa dataset (`fileData`), tên file có trọng số ngẫu nhiên
> cho mỗi item (`fileWeight`) và tên file chứa dataset đã được định dạng có xác suất ngẫu nhiên
> cho mỗi item (`fileProb`)  tương ứng.

> _Trong trường hợp muốn chạy bộ dataset đã định dạng sẵn có xác suất ngẫu nhiên và trọng số thì sẽ bỏ qua bước 1 này.
> Nhưng sẽ cần chỉnh lại tên file có trọng số ngẫu nhiên cho mỗi item (`fileWeight`) và tên file chứa dataset đã được 
> định dạng có xác suất (`fileProb`) trong hàm main của các file maintest (ví dụ: **MainTest_TUFP_Retail.java**) 
> cho phù hợp trước khi thực hiện bước 2_.

2. Chạy giải thuật TWUFP cho bộ dataset mới có trọng số và xác suất ngẫu nhiên cho mỗi item

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/ca/pfv/spmf/tools/MemoryLogger.java ./src/algorithms/TWUFP/GUI/MainTest_TWUFP_Retail.java 
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.TWUFP.GUI.MainTest_TWUFP_Retail
```

> Kết quả chạy chương trình trên đã có sẵn trong folder `src/algorithms/TWUFP/output/output_TWUFP_retail.txt`.


## Roadmap

- [x] Tìm hiểu bài báo, cài đặt được thuật toán trong bài

- [ ] Tìm kiếm thêm giải thuật để so sánh 

- [x] Thêm vào thuộc tính weighted (thuộc tính của item)

## Công nghệ sử dụng
- Ngôn ngữ: Java
- IDE: Intellij
- JDK: 15 java version "15.0.2"

## Cấu hình máy đang chạy
- Processor:	Intel(R) Core(TM) i5-1035G1 CPU @ 1.00GHz 1.19 GHz
- Installed: RAM	8.00 GB (7.78 GB usable)
- Edition:	Windows 11 Home Single Language

## Authors

- Huỳnh Huỳnh Đức (52000197) - [@HuynhDucne](https://github.com/HuynhDucne)
- Tô Ngọc Huyền (52000217) - [@Huynneh](https://github.com/Huynneh)

