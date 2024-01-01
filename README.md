
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

- Trong Command Prompt, đi tới thư mục src

```bash
  cd src
```

- Biên dịch chương trình

```bash
  javac -encoding utf8 -d . .\algorithms\TUFP\Main.java 
```

- Chạy chương trình

```bash
  java algorithms.TUFP.Main 
```
> Kết quả chạy chương trình đã có trong folder **algorithms/TUFP/Result vidumau**

## Chạy các bộ tests

Ví dụ để chạy các bộ testcases như dataset retail, thực hiện các bước sau

- Trong Command Prompt, đi tới thư mục src

```bash
  cd src
```

- Biên dịch chương trình

```bash
  javac -encoding utf8 -d . .\testcase\retail_dataset\MainTest_K900_Retail.java 
```

- Chạy chương trình

```bash
  java testcase.retail_dataset.MainTest_K900_Retail 
```

> Sau khi chạy chương trình thành công sẽ có một file ***dataset_prob.txt*** được tạo mới trong folder **testcase**.
> File ***dataset_prob.txt*** này chứa dataset được định dạng với các xác suất ngẫu nhiên

> Kết quả chạy chương trình đã có trong folder **testcase/retail_dataset/Result Retail k=900 full transaction**

## Roadmap

- [x] Tìm hiểu bài báo, cài đặt được thuật toán trong bài

- [ ] Tìm kiếm thêm giải thuật để so sánh 

- [ ] Thêm vào thuộc tính weighted (thuộc tính của item)


## Authors

- Huỳnh Huỳnh Đức (52000197) - [@HuynhDucne](https://github.com/HuynhDucne)
- Tô Ngọc Huyền (52000217) - [@Huynneh](https://github.com/Huynneh)

