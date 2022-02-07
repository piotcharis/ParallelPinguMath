**Parallel PinguMath**

Seit du unseren Partnern der PUM dabei geholfen und mit deinem Binomial Heap die Administration wieder auf Vordermann gebracht hast, haben sie endlich wieder Zeit für Forschung. Die forschenden Pinguine haben beim Mittagessen in der Mensa mitbekommen, dass du schon Erfahrungen mit Pageranking für die PPL gesammelt hast. Sie erklären dir, dass man mit einem alternativen Ansatz das Pageranking berechnen kann, genauer gesagt, mit einer quadratischen Matrix (Google Matrix). Weil du dich aktuell in deinem Studium mit Multithreading auseinandersetzt, fragst du dich, wie du Threads dazu nutzen könntest, um Standard-Operationen effizienter zu berechnen. Um dich erstmal auf das Multithreading zu fokussieren, beschränkst du dich ausschließlich auf quadratische Matrizen, bei denen die Dimension eine 2er-Potenz ist.

Hinweis 1: Sowohl bei der Addition als auch bei der Multiplikation soll das Ergebnis in einer neuen SquareMatrix zurückgegeben werden. Die beiden Matrizen die addiert bzw. multipliziert werden, dürfen nicht verändert werden.

Hinweis 2: Die beiden Methoden resetThreadCount und getThreadCount von AddComputeThread und MulComputeThread dürfen von deiner Implementierung nicht aufgerufen werden. Generell gilt: Wird der computeThreadCounter in irgend einer Form manipuliert (abgesehen vom vorgegebenen Inkrement im jeweiligen Konstruktor), wird die Abgabe ohne Ausnahmen mit 0P bewertet.

Tipp: In der Klasse Playground findest du eine Methode zum Plotten der Rechenzeit. Es lohnt sich durchaus, ein wenig mit der Größe der Matrizen oder dem Threshold minDim zu spielen, um ein Gefühl zu entwickeln, wie viel schneller eine parallele Implementierung sein kann, aber auch wie viel Overhead das Erstellen neuer Threads verursachen kann. Punkte gibt es dafür aber natürlich keine. Tipp: Solltest du bereits vor deinem Tutorium mit der Aufgabe beginnen wollen, war unsere Ehren-Pinguin-Tutorin Lara so lieb und hat euch ein kleines Erklärvideo zu dieser Aufgabe erstellt. Sollten dennoch Fragen zur Mathematik offen geblieben sein antworten wir euch wie immer in Zulip! Hier geht's zum Video von Lara

**SquareMatrix**

Die Klasse SquareMatrix stellt dir alle Funktionen zur Verfügung, die du für diese Aufgabe benötigst. Einige davon kannst du auch hervorragend zum Testen deiner Implementierung nutzen. Hier eine Kurzbeschreibung der Methoden, genaueres findest du in den Kommentaren der Implementierung. WICHTIG: Die Klasse zu Verändern ist ausdrücklich verboten.

SquareMatrix(int n) erstellt eine Matrix von Dimension n x n und füllt diese mit 0-en

SquareMatrix(SquareMatrix M11, SquareMatrix M12, SquareMatrix M21, SquareMatrix M22) erstellt eine Matrix aus den vier übergebenen Blockmatrizen.

SquareMatrix(BigInteger[][] values) erstellt eine Matrix und füllt diese mit den übergebenen Werten. 

getDimension() gibt die Dimension der Matrix zurück.

get(int i, int j) gibt den Wert der Position (i, j) zurück. 
 
set(int i, int j, BigInteger newValue) setzt den neuen Wert der Position (i, j)

getQuadrant(int i, int j) gibt die Blockmatrix der Position (i, j) zurück

setQuadrant(int i, int j, quadrant) ersetzt die Blockmatrix an Position (i, j)

negate() negiert jeden Eintrag der Matrix

transpose() transponiert die Matrix

equals() vergleicht zwei Matrizen

toString() gibt eine Stringrepräsentation der Matrix zurück

generateRandomMatrix(int n) erstellt eine Matrix von Dimension n und füllt diese mit Zufallszahlen

generateRandomMatrix(int n, Random rnd) erstellt eine Matrix von Dimension n und füllt diese mit Zufallszahlen, die vom übergebenen Random generiert werden
SquareMatrixAdd & AddComputeThread

**addParallel**

In der Klasse SquareMatrixAdd soll die Methode addParallel(SquareMatrix A, SquareMatrix B, int minDim) implementiert werden. Diese soll die Addition von zwei Matrizen realisieren. Sollten die zwei Matrizen nicht addiert werden können, muss eine IllegalArgumentException mit einer entsprechenden Nachricht("A⎵can⎵not⎵be⎵null", "B⎵can⎵not⎵be⎵null" oder "A⎵and⎵B⎵have⎵different⎵dimensions") geworfen werden. Das Argument minDim ist nach unten durch die Konstante MIN_DIM beschränkt, d.h. ist minDim kleiner als die untere Schranke, soll statt dessen der Wert von MIN_DIM genutzt werden. WICHTIG: der Wert von MIN_DIM darf von euch nicht verändert werden.

Die Idee der Implementierung ist es, rekursiv Threads zu erzeugen, die jeweils die Addition einer der vier Blockmatrizen (Quadranten) übernimmt. Dies geschieht solange, bis die Dimension der Matrizen kleiner gleich unserem Threshold minDim bzw. MIN_DIM ist. Sobald die Dimension unseren Threshold erreicht hat, wird der vorgegebene sequentielle Ansazt zur Berechnung der Addition verwendet (siehe SquareMatrixAdd.addSequential).

Die zu erzeugenden Threads (AddComputeThread) musst du natürlich noch vervollständigen. Der Konstruktor von AddComputeThread erwartet als Argumente eine threadID. Diese gibt den Index im results Array an, an dem nach der Berechnung des Threads das Ergebnis erwartet wird. Die Argumente A und B sind die beiden zu addierenden Matrizen und minDim der zuvor behandelte Threshold. Wird der Thread gestartet, soll dieser wenn nötig/erlaubt neue AddComputeThreads für jeden der vier Quadranten von A/B erstellen, um die Addition der Quadranten in den neuen Threads zu berechnen. (Tipp: SquareMatrix.getQuadrant gibt dir die entsprechenden Blockmatrizen zurück.) Final müssen diese Teilergebnisse noch zu einer Ergebnis-Matrix kombiniert werden, dazu bietet dir SquareMatrix einen passenden Konstruktor bereit.

**mulSequential**

In der Klasse SquareMatrixMul soll die Methode mulSequential(SquareMatrix A, SquareMatrix B) implementiert werden. Diese soll die Multiplikation von zwei Matrizen realisieren. Sollten die zwei Matrizen nicht multipliziert werden können, muss eine IllegalArgumentException mit einer entsprechenden Nachricht("A⎵can⎵not⎵be⎵null", "B⎵can⎵not⎵be⎵null" oder "A⎵and⎵B⎵have⎵different⎵dimensions") geworfen werden.
Für die sequentielle Implementierung, reich der naive Ansatz, den du bereits aus der Vorlesung oder Schule kennst.

**mulParallel**

In der Klasse SquareMatrixMul soll die Methode mulParallel(SquareMatrix A, SquareMatrix B, int minDim) implementiert werden. Diese soll die Multiplikation von zwei Matrizen realisieren. Sollten die zwei Matrizen nicht multipliziert werden können, muss eine IllegalArgumentException mit einer entsprechenden Nachricht("A⎵can⎵not⎵be⎵null", "B⎵can⎵not⎵be⎵null" oder "A⎵and⎵B⎵have⎵different⎵dimensions") geworfen werden. Das Argument minDim ist nach unten durch die Konstante MIN_DIM beschränkt, d.h. ist minDim kleiner als die untere Schranke, soll statt dessen der Wert von MIN_DIM genutzt werden. WICHTIG: der Wert von MIN_DIM darf von euch nicht verändert werden.

Die Idee der Implementierung ist es rekursiv Threads zu erzeugen, die jeweils die Multiplikation von Blockmatrizen (Quadranten) übernimmt. Dies geschieht solange, bis die Dimension der Matrizen kleiner gleich unserem Threshold minDim bzw. MIN_DIM ist. Sobald die Dimension unseren Threshold erreicht hat, wird der sequentielle Ansatz zur Berechnung der Multiplikation verwendet (siehe SquareMatrixMul.mulSequential).

Die zu erzeugenden Threads (MulComputeThread) musst du natürlich noch vervollständigen. Der Konstruktor von MulComputeThread erwartet als Argumente eine threadID. Diese gibt den Index im results Array an, an dem nach der Berechnung des Threads das Ergebnis erwartet wird. Die Argumente A und B sind die beiden zu multiplizierenden Matrizen und minDim der zuvor behandelte Threshold. Im Gegensatz zur Blockmatrix Addition gestaltet sich die Multiplikation etwas komplexer, aber keine Sorge wir gehen das Verfahren Schritt für Schritt durch. Es ist nicht notwendig, dass du verstehst, wieso es funktioniert.
