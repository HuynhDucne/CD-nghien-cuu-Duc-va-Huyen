
# Chuyên Đề Nguyên Cứu 1

Mining top-k weighted frequent itemsets from uncertain databases

## Nhóm sinh viên

- Huỳnh Huỳnh Đức (52000197) - [@HuynhDucne](https://github.com/HuynhDucne)
- Tô Ngọc Huyền (52000217) - [@Huynneh](https://github.com/Huynneh)

## Nội dung

- [Cải biên dựa trên TUFP] Giải thuật Top-k Uncertain Weighted Frequent Itemsets (TUWFI) - trong folder `tuwfi`
- [Cài đặt tối ưu cho TUWFI] Giải thuật Uncertain Weighted Itemsets - Optimal (TUWFI-Optimal) - trong folder `tuwfioptimal`

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

## Chạy ví dụ mẫu trong bài báo với giải thuật TUWFI

Để chạy ví dụ mẫu trong bài báo, thực hiện tuần tự các bước sau:

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. 
  Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/tuwfi/gui/TestTuwfiExample.java  
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.tuwfi.gui.TestTuwfiExample  
```

> Sau khi chạy chương trình thành công, sẽ có thông báo yêu cầu người dùng nhập từ bàn phím một giá trị k để lấy 
>Top-k Uncertain Weighted Frequent Itemsets. Sau khi nhập k và ấn Enter, giải thuật TUWFI sẽ bắt đầu
>khai phá Top-k UWFIs.

>Kết quả Top-k UWFIs sẽ lưu lại vào folder `src/algorithms/tuwfi/result/`. Tên file chứa kết quả 
>sẽ thay đổi theo tham số k mà người dùng nhập. Ví dụ: nhập k=5 thì tên file chứa kết quả là
>`result_TUWFI_example_k_5.txt`


## Chạy các bộ tests với giải thuật TUWFI

Ví dụ để chạy giải thuật TUWFI cho bộ dataset **_foodmart_**, thực hiện các 2 bước sau:

1. Tạo một file chứa trọng số (weight) của các item và một file dataset mới có xác suất tồn tại ngẫu nhiên cho mỗi item trong bộ dataset cần test

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/tuwfi/gui/DatasetGui.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.tuwfi.gui.DatasetGui
```

> Sau khi chạy chương trình thành công:
>- Tạo ra file **foodmart_weight.txt** có chứa trọng số ngẫu nhiên cho mỗi item.
   > File được lưu trong thư mục `src/dataset/weight`.
>- Tạo ra  file **foodmart_prob.txt** có chứa xác suất tồn tại ngẫu nhiên cho mỗi item.
   > File được lưu trong thư mục `src/dataset/probability`.

> Trong folder `src/dataset/origin` sẽ chứa các bộ dataset truyền thống có thể test

> Trong trường hợp muốn chạy các bộ dataset khác thì cần vào file **DatasetGui.java**
> để điều chỉnh tên file chứa dataset (`originFile`), tên file có trọng số ngẫu nhiên
> cho mỗi item (`weightFile`) và tên file chứa dataset đã được định dạng có xác suất
> tồn tại ngẫu nhiên cho mỗi item (`probFile`) tương ứng.

> _Trong trường hợp muốn chạy bộ dataset đã định dạng sẵn có xác suất ngẫu nhiên và trọng số thì sẽ bỏ qua bước 1 này.
> Nhưng sẽ cần chỉnh lại tên file có trọng số ngẫu nhiên cho mỗi item (`weightFile`) và tên file chứa dataset đã được
> định dạng có xác suất (`probFile`) trong hàm main của các file test (ví dụ: **TestTuwfiFoodmart.java**)
> cho phù hợp trước khi thực hiện bước 2_.


2. Chạy giải thuật TUWFI cho bộ dataset mới có xác suất tồn tại ngẫu nhiên cho mỗi item

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/tuwfi/gui/TestTuwfiFoodmart.java 
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.tuwfi.gui.TestTuwfiFoodmart
 
```

> Sau khi chạy chương trình thành công, sẽ có thông báo yêu cầu người dùng nhập từ bàn phím giá trị k để lấy
>Top-k Uncertain Weighted Frequent Itemsets. Sau khi nhập top-k và ấn Enter, giải thuật TUWFI sẽ bắt đầu
>khai phá Top-k UWFIs.

>Kết quả Top-k UWFIs sẽ lưu lại vào folder `src/algorithms/tuwfi/result/`. Tên file chứa kết quả
>sẽ thay đổi theo tham số k mà người dùng nhập. Ví dụ: nhập k=100 thì tên file chứa kết quả là
>`result_TUWFI_foodmart_k_100.txt`


## Chạy các bộ tests với giải thuật TUWFI-Optimal

Ví dụ để chạy giải thuật TUWFI-Optimal cho bộ dataset **_foodmart_**, thực hiện các 2 bước sau:

1. Tạo một file chứa trọng số (weight) của các item và một file dataset mới có xác suất tồn tại ngẫu nhiên cho mỗi item từ bộ dataset cần test

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/tuwfioptimal/gui/DatasetGui.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.tuwfioptimal.gui.DatasetGui
```

> Sau khi chạy chương trình thành công: 
>- Tạo ra file **foodmart_weight.txt** có chứa trọng số ngẫu nhiên cho mỗi item.
> File được lưu trong thư mục `src/dataset/weight`.
>- Tạo ra  file **foodmart_prob.txt** có chứa xác suất tồn tại ngẫu nhiên cho mỗi item.
> File được lưu trong thư mục `src/dataset/probability`.

> Trong folder `src/dataset/origin` sẽ chứa các bộ dataset truyền thống có thể test

> Trong trường hợp muốn chạy các bộ dataset khác thì cần vào file **DatasetGui.java**
> để điều chỉnh tên file chứa dataset (`originFile`), tên file có trọng số ngẫu nhiên
> cho mỗi item (`weightFile`) và tên file chứa dataset đã được định dạng có xác suất 
> tồn tại ngẫu nhiên cho mỗi item (`probFile`) tương ứng.

> _Trong trường hợp muốn chạy bộ dataset đã định dạng sẵn có xác suất ngẫu nhiên và trọng số thì sẽ bỏ qua bước 1 này.
> Nhưng sẽ cần chỉnh lại tên file có trọng số ngẫu nhiên cho mỗi item (`weightFile`) và tên file chứa dataset đã được 
> định dạng có xác suất (`probFile`) trong hàm main của các file test (ví dụ: **TestTuwfiOptimalFoodmart.java**) 
> cho phù hợp trước khi thực hiện bước 2_.

2. Chạy giải thuật TUWFI-Optimal cho bộ dataset mới có trọng số và xác suất tồn tại ngẫu nhiên cho mỗi item

- Kiểm tra nếu chưa có thư mục `bin` thì cần tạo thư mục bin chứa file .class khi biên dịch. Nếu đã có thư mục `bin` thì bỏ qua lệnh này:

```bash
  mkdir bin
```

- Biên dịch chương trình:

```bash
  javac -encoding utf8 -d bin -cp "./src" ./src/algorithms/tuwfioptimal/gui/TestTuwfiOptimalFoodmart.java
```

- Chạy chương trình:

```bash
  java -cp bin algorithms.tuwfioptimal.gui.TestTuwfiOptimalFoodmart
```

> Sau khi chạy chương trình thành công, sẽ có thông báo yêu cầu người dùng nhập từ bàn phím giá trị k để lấy
>Top-k Uncertain Weighted Frequent Itemsets. Sau khi nhập top-k và ấn Enter, giải thuật TUWFI-Optimal sẽ bắt đầu
>khai phá Top-k UWFIs.

>Kết quả Top-k UWFIs sẽ lưu lại vào folder `src/algorithms/tuwfioptimal/result/`. Tên file chứa kết quả
>sẽ thay đổi theo tham số k mà người dùng nhập. Ví dụ: nhập k=100 thì tên file chứa kết quả là
>`result_TUWFI_optimal_foodmart_k_100.txt`

## Công nghệ sử dụng
- Ngôn ngữ: Java
- IDE: Intellij IDEA
- JDK: 15 java version "15.0.2"

## Cấu hình máy đang chạy
- Processor:	Intel(R) Core(TM) i5-1035G1 CPU @ 1.00GHz 1.19 GHz
- Installed: RAM	8.00 GB (7.78 GB usable)
- Edition:	Windows 11 Home Single Language



