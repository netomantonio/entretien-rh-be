package br.ufpr.tcc.entretien.backend.common.exception.jwt

class TokenGeneratorException (s: String, exception: Exception): RuntimeException(s, exception)