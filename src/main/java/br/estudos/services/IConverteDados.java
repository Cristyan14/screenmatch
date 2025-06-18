package br.estudos.services;

public interface IConverteDados {
	<T> T obterDados(String json, Class<T> classe);
}
