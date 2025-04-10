# OTI-Projekti

Branch organisaatio: <br>
"develop" branchiin voi tallentaa muutoksia vapaasti. <br>
"main" branchiin tallennetaan valmiita toimintoja.

Käytännöllisiä github komentoja:

Github repositioon yhdistäminen: <br>
Intellij: File -> New -> Project from Version Control -> https://github.com/Sakaarii/OTI-Projekti.git <br>
(Kaikki alla olevat komennot löytyvät myös intellij version hallinnasta)

uudituksien vetäminen: <br>
git pull https://github.com/Sakaarii/OTI-Projekti.git

Näyttää kaikki branchit: <br>
git branch -a 

Vaihtaa brachia: <br>
git checkout [branchin nimi]

Muutosten lisääminen paikallisesti: <br>
git add . <br>
git commit -m "whatever"

Muutosten lisääminen onlineen: <br>

Jos ensimmäinen push: <br>
git push -u origin develop

Muuten: <br>
git push

Jos haluat laittaa development branchin muutokset mainiin: <br>
git checkout master <br>
git merge develop <br>
git push


