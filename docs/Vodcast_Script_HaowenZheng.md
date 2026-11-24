# Vodcast Script

**COM102 Object Oriented Programming — Practical Skills Assessment 2**

Scenario B: Library Management System · Haowen Zheng · Student ID: STUDENT_ID

Target length: 4 minutes 50 seconds (maximum 5 minutes). Spoken part in English, production notes in Chinese.

## Before recording

- Reset the data file before recording so the demo starts from the same records: git checkout -- data/library-data.txt
- Open the five files in tabs in advance: Main.java, Library.java, Resource.java, EBook.java and LibraryTest.java.
- Use a large editor and console font; the recording is compressed and small text becomes unreadable.
- Record at 1920x1080, 30 fps, H.264 MP4, and keep the total length under five minutes.
- Put the webcam in the bottom-right corner and keep your face visible for the whole video.
- Close notifications and anything private, and speak slowly; the script is timed for just under five minutes at a normal pace.
- Do not read code line by line. Point at the class or method and explain the decision behind it.

## Script

### 0:00 – 0:25 — Introduction

Hello, my name is Haowen Zheng, and this is my COM102 Practical Skills Assessment 2. I chose Scenario B, the library management system, written in Java as a console application. In the next five minutes I will show the program running, explain the main classes, and finish with what works and what does not.

> 开场保持简短，画面先给 IDE 全屏，摄像头在右下角。语速放慢，第一句说完停半秒再继续。

### 0:25 – 0:50 — Starting the program

This is the project in IntelliJ IDEA. I run the Main class. The program reads data/library-data.txt and reports six resources and three members, so records from the last session are already loaded. The menu has nine options; the main ones are add, remove, search, borrow, return and save.

> 运行前先把 data/library-data.txt 还原成仓库里的版本，保证每次录像输出一致。让控制台字号大一点，观众要能看清。

### 0:50 – 1:25 — Adding and searching

I add a book: option 1, then 1. ID R007, Design Patterns, 1994, Erich Gamma, Programming. It is confirmed and shown as available. Now a bad record: year 1200 is rejected by the constructor, so nothing is added. For search I choose option 4 and type 'java' in lower case; it still finds Effective Java, because the search ignores case.

> 这一段要连续操作，不要剪太碎。输入错误年份时停一下，让错误信息留在屏幕上一两秒。

### 1:25 – 2:05 — Borrowing and returning

Next I borrow Clean Code for member M002. The due date is two weeks away. Borrowing the same book again is refused because it is already on loan. The DVD R004 was due on 21 November, so it is overdue; returning it prints a fine based on the late days and the DVD rate of one pound a day. Option 9 shows the history: the return is there with its dates and fine. Option 0 saves and exits. I start again, and the records, the loan and its history are loaded.

> 还 DVD 前先确认当天日期晚于 11 月 21 日，这样一定会出现罚款。退出重开后，把“Loaded 6 resources…”这一行停留两秒，作为持久化的证据。

### 2:05 – 2:40 — The class structure

Here is the design. Borrowable is the interface: an item can be borrowed, returned, and asked for its loan period and fine. Resource is abstract. It holds the id, title, year and loan state, and implements borrow and returnItem once, so the four types do not repeat that code. Book, Magazine, DVD and EBook extend it and supply only what differs. LoanRecord is separate: one record per loan, closed when the item comes back, so the history survives the shelf.

> 开四个标签页：Borrowable、Resource、EBook、Library。讲接口时鼠标划过方法名，不要逐行念代码。

### 2:40 – 3:25 — Polymorphism and data handling

The fine is the clearest example of polymorphism. Resource.calculateFine uses getDailyFine from the subclass: twenty-five pence a day for a book, ten for a magazine, one pound for a DVD. EBook overrides calculateFine and returns zero, because access ends with the loan. The menu never checks the type; it calls the method on a Resource reference and the right version runs. Library uses two ArrayLists. Borrowing checks the item, the member, availability, and the three-item limit. Saving writes one line per record; loading reads members first, then resources, because a resource line only stores the borrower's ID.

> 讲多态时可以打开 EBook.java 和 Resource.java 对照，指出 override 的位置。这一段是评分重点，不要赶。

### 3:25 – 4:00 — Testing and self-assessment

I tested with JUnit 5. LibraryTest has sixteen tests: borrowing, the three-item limit, the fine boundary on the due date and one day later, the e-book rule, duplicate IDs, removing a loaned item, save and load, the loan history, and a damaged line. All sixteen pass. One test failed at first: a bad date threw an exception my loader did not catch, so I changed it to skip the bad line and count it. The required features are all there; reservations and payment tracking are missing.

> 切到测试结果截图或直接跑 LibraryTest，让绿色对勾和 12/12 出现在画面上。

### 4:00 – 4:35 — Known bugs and limitations

There are known gaps. Fines are recorded in the history, but there is no flag for whether a fine was paid, so the library cannot show an outstanding balance. There is no reservation queue. Saving writes straight to the file, so a crash during a save could leave it half written. One record means one copy, so two copies need two IDs. Members cannot be removed, and dates come from the system clock, so a loan cannot be backdated from the menu. Payment tracking is what I would fix next.

> 这段要诚实、具体，不要只说“功能还不完善”。每条都指一下对应的代码位置，比如说保存直接写文件时打开 Library.saveData。

### 4:35 – 4:50 — Closing

That is the system. The code, tests and documents are in the GitHub repository PSA2-COM102-Zheng-Haowen. Thank you for watching.

> 结尾可以回到项目目录或 GitHub 页面，画面停两秒再停止录制。

## Known bugs to mention

- No loan history and no fine payment record; the fine is calculated and printed only.
- No reservations for items that are already on loan.
- saveData writes directly to the target file, so an interrupted save can truncate the data.
- One record represents one copy; multiple copies need separate IDs.
- Members cannot be removed, and dates come from the system clock, so a loan cannot be backdated from the menu.
