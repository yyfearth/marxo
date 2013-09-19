"use strict"

define 'crypto', ['lib/crypto-js'], (CryptoJS) =>
  COMMON_SALT = 'MARXO'

  hashPassword: (email, password) ->
    salt = CryptoJS.algo.HMAC.create(CryptoJS.algo.SHA256, COMMON_SALT).update(email).finalize()
    hash = CryptoJS.PBKDF2 password, salt, hasher: CryptoJS.algo.SHA256, keySize: 256/32, iterations: 1024
    hash = hash.toString CryptoJS.enc.Base64
    hash[0...-1]

  md5Email: (email) ->
    CryptoJS.MD5(email).toString CryptoJS.enc.Hex
