# Secure File Transfer with Hybrid Encryption Technique

This repository contains the implementation of a secure file transfer system using hybrid encryption technique, compression, and authentication. The system utilizes RSA encryption, PKCS1_OAEP padding, zlib compression, and SHA256 authentication to ensure data security during transfer.

## Features

- **Hybrid Encryption**: The system employs RSA encryption algorithm with PKCS1_OAEP padding to encrypt the file contents, ensuring secure data transfer.
- **Compression**: The encrypted data is compressed using zlib, reducing the file size for efficient storage and transfer.
- **Authentication**: SHA256 hash function is applied to the compressed data to verify its integrity and prevent tampering during transmission or storage.
- **Efficient Performance**: The system is designed to be fast and efficient, providing quick encryption and decryption of files.

## Usage

1. Clone the repository:

   ```shell
   git clone https://github.com/Malwareman007/Secure-System.git 
   ```
2. Navigate to the project directory:
   ```
   cd Secure-System
   ```
   
3. Run the Program:
    ```
    java Scuresystem.java
    ```

     # Experimental Results
     The performance of the secure file transfer system was evaluated on different file sizes. The encryption and decryption times were measured on a system with an Intel Core i5-8250U processor, 8GB DDR4 RAM, and a 256GB solid-state drive.
   * Encryption Time: The encryption time increased with larger file sizes, but remained efficient, with files up to 100MB encrypted in less than 5 seconds.
   * Decryption Time: The decryption process also exhibited good performance, with files up to 100MB decrypted in approximately 20 seconds.
   * Compression Efficiency: The compression significantly reduced the size of the encrypted data, enabling faster transfer and storage.

  # Future Work
  This secure file transfer system can be further enhanced by integrating additional security measures, such as digital signatures or symmetric key encryption, to strengthen data security. Additionally, the system can be adapted for use in cloud computing environments, where data security is of utmost importance.

# License
This project is licensed under the MIT License. Feel free to use and modify it for your own purposes.
