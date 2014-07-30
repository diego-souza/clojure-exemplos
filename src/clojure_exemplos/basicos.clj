; Isto é um comentário

; Definindo algo

(def a 1)

(def b (fn [x] x))

; Avaliando a
a

; Avaliando b
; Repare que a avaliação de "b" retorna a função.
; É um pouco diferente do que seria em ruby por exemplo.
; Onde "b" seria a chamada da função
b

; Avaliando (b x)
; Agora sim chamamos a função.
; Como derivada de Lisp o primeiro simbolo de uma lista
; é um operador que será executado. Mais sobre isso em breve.
(b 3)

; Avaliando (b a)
(b a)

; === Estruturas de dados ===

; Listas
; São listas encadeadas simples.
; Tem custo de acesso O(n)
; Repare que como o primeiro simbolo de uma lista é um operador
; devemos usar a função "quote" para que o numero "1" não seja executado
(quote (1 2 3 4 5))
; Outra opção é a utilização de "'"
; Cujo efeito é o mesmo
'(1 2 3 4 5)
(list 1 2 3 4 5)
(def l '(1 2 3 4 5))

; Vetores
; São listas indexadas.
; Tem custo de acesso muito baixo O(log32 n).
; De acordo com o criador da linguagem para as maquinas de hoje isso
; é bom o suficiente para ser considerado constante, ou seja, O(1).
[1 2 3 4 5]
(vector 1 2 3 4 5)
(def v [1 2 3 4 5])

; Mapas
; São estruturas de chave/valor.
; Custo de acesso igual ao dos vetores.
; Não tem ordenação embora exista a possibilidade de serem
; caso sejam criados com a função sorted-map
{:a 1 :b 2}
(hash-map :a 1 :b 2)
(def m {:a 1 :b 2})

; Conjuntos
; Seguem a teoria dos conjuntos.
; Não contem elementos repetidos
#{1 2 3 4 5}
(set '(1 2 3 4 5))
(set [1 2 3 4 5])
(def s (set '(1 2 3 4 5)))


; Funções genéricas que operam em estruturas de dados

(count v)
(empty? v)

; Estas operações servem para transformar uma estrutura de um tipo em outro.
(into '() v)
(into #{} v)
(into [] l)

; Funções de listas

(first l)
(rest l)
(rest v)
(cons 6 l)
; Repare que o acesso é O(n)
(nth l 3)

; Funções de vetores
(v 3)
(subvec v 3 5)
; Repare que o acesso é O(log32 n)
(nth v 3)

; Funções de mapas
(:a m)
(m :a)
(get m :a)
(keys m)
(vals m)

(assoc m :a 3)
(assoc m :c 3)
(dissoc m :a)


(assoc m :a {:c 3})
(assoc-in {:a {:c 3} :b 2} [:a :c] 5)


(merge m {:a 3 :c 3})

; Funções de conjuntos
(contains? s 3)
; A meioria das funções que trabalham com conjuntos está separada em outro namespace
; Por isso temos que importá-lo antes de usa-las
(use 'clojure.set)
(superset? s #{1 2})
(difference s #{1 2})

; ==== Sequencias ===

; Sequencia é uma interface baseada nas operações de listas de Lisp
; As operações são
; Seq - retorna uma sequencia caso hava algum elemento na coloção
(seq l)
; Ou nil caso esteja vazia.
; Embora possa não parecer, o fato de retornar nil quando for vazia simplifica vários
; códigos que utilizam sequencias. Isto acontece porque nil é o único valor além de false
; que é interpretado como falso em clojure.
(seq '())
; First - retorna o primeiro elemento da sequencia
(first l)
; Rest - retorna todos os elementos menos o primeiro
(rest l)
; Cons - adiciona um elemento ao inicio da sequencia
(cons 9 l)

; Todas as estruturas de dados de clojure implementam uma interface para Sequencias.
(seq l)
(seq v)
(seq m)
(seq s)

; Isso faz com que funções como conj, que opera em todas as estruturas de dados,
; sejam possíveis já que internamente usam sequencias para sua implementação.
(conj '(1 2 3 4 5) 6 7)

(conj [1 2 3 4 5] 6 7)

(conj {:a 1 :b 2} [:c 3] [:d 4])

(conj #{1 2 3 4 5} 6 7)

; Entretanto, tenha cuidado. Leia a documentação das funções para saber
; que tipo de estrutura elas retornam.
; Veja o exemplo abaixo.

(rest v)
(list? (rest v))
(coll? (rest v))
(seq? (rest v))

; Sequencia preguiçosa/atrasada (Lazy Sequence)
; Este tipo de sequencia não é avaliada no momento em que é definida.
; Apenas avalia cada elemento quando for utilizado.
; Isto nos possibilita a criação de sequencias infinitas.
; Além de possibilitar a escrita de códigos de uma maneira diferente.

; Veja essa sequencia por exemplo
; Ela define que quando for chamada sem parametros ela utilizará o 1 como inicio.
; Ela é formada pela construção do valor inicial com a sequencia que resulta dela mesma
; com o valor inicial incrementado. Gerando assim a sequencia de todos os numeros positivos.

(defn numeros-positivos
    ([] (numeros-positivos 1))
    ([n] (cons n (lazy-seq (numeros-positivos (inc n))))))

; Claro que se tentarmos utilizar toda a sequencia entraremos em um loop infinito
; já que a sequencia nunca acaba.
; Temos então algumas funções para lidar com essas sequencias.

; A função take retorna a seqeuncia dos primeiros n elementos da sequencia.
(take 5 (numeros-positivos))

; A função drop retorna a sequencia sem os primeiros n elementos.
(drop 5 (numeros-positivos))

; A função map retorna outra sequencia em que cada elementos é o resultado
; da aplicação da função passada como parâmetro nos elementos da seqeuncia anterior.
(map inc (numeros-positivos))

; A função filter retorna outra sequencia em que apenas os elementos cujo resultado
; da aplicação da função passada retornam true
(filter even? (numeros-positivos))

; Repare porém que o retorno de take, drop, map e filter também são sequencias atrasadas.
; Embora seja possivel retornar todos os elementos de take ela também é atrasada.
; Já para as outras; voltar todos os elementos significaria um loop infinito.
; Podemos ver isto através dos exemplos abaixo.
; A função doall força a avaliação de todos os elementos da sequencia.
; A função realized? diz se a sequencia teve sua avaliação forçada.

(realized? (take 5 (numeros-positivos)))
(realized? (drop 5 (numeros-positivos)))
(realized? (doall (take 5 (numeros-positivos))))

; Podemos criar várias sequencias desta forma como por exemplo a de Fibonacci
(defn fib [a b] (cons a (lazy-seq (fib b (+ b a)))))

(take 5 (fib 1 1))

(take 10 (fib 10 11))

; Além disso clojure tem funções para auxiliar na criação destas sequencias

(iterate inc 5)

(cycle [1 2 3 4])

(repeat 10)

(repeatedly #(rand-int 11))

; Deve-se tomar cuidado porém com a natureza das seqeuncias infinitas
; Nunca chame uma função que não retorne uma sequencia atrasada em uma seqeuncia infinita
; Não faça isso:
; (reduce + (positive-numbers))
; (reverse (positive-numbers))
; (shuffle (positive-numbers))...

; Podemos porém utiliza-las em conjunto com a função take
(reduce + (take 10 (positive-numbers)))

(reverse (take 5 (positive-numbers)))

(split-at 3 (take 5 (positive-numbers)))

(partition 3 (take 10 (positive-numbers)))

(filter even? (take 10 (positive-numbers)))

(group-by even? (take 10 (positive-numbers)))

(group-by count ["a" "as" "asd" "asdf" "b" "bds" "tarsd"])

; Atenção ao tipo de retorno
; A função shuffle retorna um vetor ao invés de sequencia.
; Provavelmente isto acontece por questões de performace.
; Se formos até o código da função veremos que na verdade é criado um Array Java
; a partir da sequencia e que o método shuffle de Collections de Java é utilizado.
; Posteriormente convertendo o resultado para um novo vetor de clojure.
(shuffle (take 5 (positive-numbers)))

(vector? (shuffle (take 5 (positive-numbers))))
(seq? (shuffle (take 5 (positive-numbers))))


; === Formas Especiais ===

; A forma especial if analisa a primeira expressão.
; Caso ela retorne falso ou nil ela executa a terceira expressão.
; Caso contrário executa a segunda.
(if (= 1 2) true false)


; A forma especial let permite a vinculação (binding) de um identificador a um valor.
; Estas vinculações são escritas na forma de identificador seguido de valor dentro de um vetores.
; Elas valem apenas dentro do escopo criado pela forma let.
; A forma especial retorna o resultado da expressão posterior ao vetor.
(defn numero-de-combinacoes [n k]
  (let [fatorial (fn [x] (reduce * (range 1 (+ x 1))))
        n!       (fatorial n)
        k!       (fatorial k)
        n-k!     (fatorial (- n k))]
    (/ n! (* k! n-k!))))

(numero-de-combinacoes 5 3)

; Alguns exemplos de outras formas especiais
(for [x [0 1 2 3 4 5]
      :let [y (* x 3)]
      :when (even? y)]
  y)

(doseq [x [1 2 3]
        y [1 2 3]]
  (prn (* x y)))


; === Interoperabilidade com Java ===

(import 'java.util.Date)
(new Date)
(. (new Date) toString)


; === Macros ===

; Segundo a Wikipédia:
; Macro, na Ciência da Computação, é uma abstração que define
; como um padrão de entrada deve ser substituído por um padrão de saída,
; de acordo com um conjunto de regras.
; Macros de programação possuem um programa de computador como entrada e retornam
; como saída um novo e expandido programa.

; Como exemplo podemos utilizar a função macroexpand-1
; Essa função substitui a entrada da macro pela saída sem executa-la
; Vemos então que defn é uma macro e que na verdade quando a chamamos
; o que estamos fazendo é chamando as formas especiais def e fn
(macroexpand-1 '(defn add [x y] (+ x y)))

; Para o próximo exemplo vamos usar a função fatorial.
(defn factorial [n]
  (if (< n 1)
    1
    (reduce * (range 1 (inc n)))))

; A macro ->> insere o resultado da primeira expressão como o ultimo parametro da próxima.
(->> 10
     (inc)
     (range)
     (map factorial)
     (filter even?)
     (reduce +))

; Ele é uma forma interessante de organização de códigos que normalmente seriam escritos de forma aninhada.
(macroexpand-1   '(->> 10
                      (+ 1)
                      (range)
                      (map factorial)
                      (filter even?)
                      (reduce +)))


; Macros vs Ruby eval ou Porque a homoiconiedade importa.
; Metaprogramação em ruby é dependente de strings devido a syntaxe da linguagem.
; Como clojure é homoiconica, o código é escrito da mesma forma que os dados; logo,
; manipular código é tão simples quanto manipular os dados. Fazendo da metaprogramação
; algo mais simples de ser alcançado

;Função em ruby que imprime um s;imbolo como uma keyword

; >> def print_sym(x)
; >>   code = "p(" + x + ".to_sym)"
; >> end
; nil
; >> eval print_sym "\"foo\""
; :foo
; nil

; Função em clojure que faz o mesmo.
(defmacro print-keyword [x] `(println (keyword ~x)))
(print-keyword 'foo)


; Algumas outras macros

(defmacro foreach [[sym coll] & body]
  `(loop [coll# ~coll]
    (when-let [[~sym & xs#] (seq coll#)]
      ~@body
      (recur xs#))))

(foreach [x [1 2 3]] (println x))


; As macros dão um poder enorme ao programador.
; A biblioteca walk implementa várias funções que percorrem estruturas
; No caso a seguir ela a estrutura passada para a macro reverse-it é percorrida,
; cada simbolo é tranformado em string, a string é revertida e depois
; transformada novamente em símbolo.
(require '(clojure [string :as str] [walk :as walk]))
(defmacro reverse-it [form]
  (walk/postwalk #(if (symbol? %) (symbol (str/reverse (name %))) %) form))

; Fazendo com que os seguintes trechos de código funcionem.
(reverse-it (nltnirp "foo"))

(reverse-it
  (qesod [gra (egnar 5)]
    (nltnirp (cni gra))))

; Veja através da macroexpand-1 o que realmente é executado.
(macroexpand-1 '(reverse-it
  (qesod [gra (egnar 5)]
    (nltnirp (cni gra)))))

; Embora seja um exemplo inutil ele ilustra o poder que as macros tem.
(reverse-it (ti-esrever (println "foo")))

; Veja que macroexpand-1 realiza apenas uma expanção.
(macroexpand-1 '(reverse-it (ti-esrever (println "foo"))))

; Abaixo, macroexpand expande as macros até que chegue ao código que será executado.
(macroexpand '(reverse-it (ti-esrever (println "foo"))))

; Quando usar

(defn fn-hello [x] (str "Hello, " x "!"))
(defmacro macro-hello [x] `(str "Hello, " ~x "!"))

; Parece que funciona da mesma forma.
(fn-hello "Brian")
(macro-hello "Brian")

; Mas não funciona...
(map fn-hello ["Brian" "Not Brian"])
(map macro-hello ["Brian" "Not Brian"])

; Para fazer a macro funcionar nesta situação
; seria necessário a definição de uma função anônima.
; O que faz com que o código fique mais complicado que o necessário.
(map #(macro-hello %) ["Brian" "Not Brian"])

; Macros são disponíveis durante a compilação e não durante a execução
; Macros deveriam ser utilizados apenas quando se tem necessidade dos próprios blocos da linguagem, como:
; - Avaliação especial de semântica
; - Sintaxe personalizada para padrões frequentes ou notação específica de domínio
; - Para ganhar vantagem pré-computando dados intermediários durante a compilação

; Colocando de forma mais simples:
; Se é possivel realizar com uma função não utilize macros.






