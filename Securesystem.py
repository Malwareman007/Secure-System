from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import zlib
import hashlib

# Generate random key pair
key = RSA.generate(2048)

# Save private key to file
with open('key.txt', 'wb') as f:
    f.write(key.export_key())

# Get public key
public_key = key.publickey()

# Ask user for file to encrypt
filename = input("Enter filename to encrypt: ")

# Read file contents
with open(filename, 'rb') as f:
    file_contents = f.read()

# Encrypt file contents with public key
cipher = PKCS1_OAEP.new(public_key)
encrypted_data = cipher.encrypt(file_contents)

# Compress encrypted data
compressed_data = zlib.compress(encrypted_data)

# Generate authentication hash of compressed data
hash_object = hashlib.sha256()
hash_object.update(compressed_data)
digest = hash_object.digest()

# Write compressed data and digest to file
with open(filename + '.enc', 'wb') as f:
    f.write(compressed_data)
    f.write(digest)

print('Encryption successful!')
print('Hash:', digest.hex())

# Decrypt file
with open('key.txt', 'rb') as f:
    private_key = RSA.import_key(f.read())

# Read encrypted file contents
with open(filename + '.enc', 'rb') as f:
    encrypted_data = f.read()

# Separate compressed data and digest
compressed_data = encrypted_data[:-32]
digest = encrypted_data[-32:]

# Verify digest
hash_object = hashlib.sha256()
hash_object.update(compressed_data)
if hash_object.digest() != digest:
    print('Authentication failed!')
    exit()

# Decompress encrypted data
encrypted_data = zlib.decompress(compressed_data)

# Decrypt encrypted data with private key
cipher = PKCS1_OAEP.new(private_key)
file_contents = cipher.decrypt(encrypted_data)

# Write decrypted file contents to file
with open(filename + '.dec', 'wb') as f:
    f.write(file_contents)

print('Decryption successful!')
print('Hash:', digest.hex())
