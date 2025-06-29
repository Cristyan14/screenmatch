package br.estudos.principal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import br.estudos.services.ConsumoApi;
import br.estudos.services.ConverteDados;
import br.model.DadosEpisodio;
import br.model.DadosSerie;
import br.model.DadosTemporada;
import br.model.Episodio;
import br.model.Serie;
import br.repository.SerieRepository;


public class Principal {
	private ConsumoApi consumo = new ConsumoApi();
	private Scanner leitura = new Scanner(System.in);
	private ConverteDados conversor = new ConverteDados();
	private  final String ENDERECO = "https://www.omdbapi.com/?t=";
	private final String API_KEY = "&apikey=ff6808f";
	private List<DadosSerie> dadosSeries = new ArrayList<DadosSerie>();
	private List<Serie> series = new ArrayList<Serie>();
	private SerieRepository repositorio;

		 public Principal(SerieRepository repositorio) {
		// TODO Auto-generated constructor stub
			 this.repositorio = repositorio;
	}

		public void exibeMenu() {
			 	var opcao = -1;
			 	while(opcao != 0) {
		        var menu = """
		                1 - Buscar séries
		                2 - Buscar episódios
		                3 - Listar séries buscadas
		                
		                0 - Sair                                 
		                """;

		        System.out.println(menu);
		        opcao = leitura.nextInt();
		        leitura.nextLine();

		        switch (opcao) {
		            case 1:
		                buscarSerieWeb();
		                break;
		            case 2:
		                buscarEpisodioPorSerie();
		                break;
		            case 3:
		                listarSeriesBuscadas();
		                break;
		            case 0:
		                System.out.println("Saindo...");
		                break;
		            default:
		                System.out.println("Opção inválida");
		        }
			 	}
		    }

		    private void listarSeriesBuscadas() {
		    	series = repositorio.findAll();
		    	series.stream().sorted(Comparator.comparing(Serie::getGenero)).forEach(System.out::println);;
		 
		    }
		    
		    private void buscarSerieWeb() {
		        DadosSerie dados = getDadosSerie();
		        //dadosSeries.add(dados);
		        Serie serie = new Serie(dados);
		        repositorio.save(serie);
		        System.out.println(dados);
		    }


		    private DadosSerie getDadosSerie() {
		        System.out.println("Digite o nome da série para busca");
		        var nomeSerie = leitura.nextLine();
		        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
		        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		        return dados;
		    }

		    private void buscarEpisodioPorSerie(){
		    	listarSeriesBuscadas();
		    	System.out.println("Escolha uma série: ");
		        var nomeSerie = leitura.nextLine();
		        
		        Optional<Serie> serie = series.stream().filter(s -> s.getTitulo().toLowerCase().contains(nomeSerie.toLowerCase()))
		        .findFirst();
		        
		        if(serie.isPresent()) {
		        	var serieEncontrada = serie.get();
		        	List<DadosTemporada> temporadas = new ArrayList<>();

			        for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
			            var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
			            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
			            temporadas.add(dadosTemporada);
			        }
			        temporadas.forEach(System.out::println);
			        
			        List<Episodio> episodios = temporadas.stream()
			        		.flatMap(d -> d.episodios().stream()
			        		.map(e -> new Episodio(d.numero(), e)))
			        		.collect(Collectors.toList());
			        serieEncontrada.setEpisodios(episodios);
			        repositorio.save(serieEncontrada);
			        
		        }else {
		        	System.out.println("Série não encontrada!");
		        }
		        
		        
		    }
}
