package com.services.internetsample

sealed class Response

class Success<T>(val data: T): Response()

class Error<T>(val error: String): Response()