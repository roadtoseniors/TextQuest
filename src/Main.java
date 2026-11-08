
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Quest quest = new Quest();
        quest.start();
    }

    public static int readNumber() {
        while (!scanner.hasNextInt()) {
            System.out.println("Ошибка! Введите число.");
            scanner.next();
            System.out.print("Ваш выбор: ");
        }

        int number = scanner.nextInt();
        scanner.nextLine();
        return number;
    }
}

class Item {
    private String name;
    private String description;
    private int heal;
    private boolean usable;

    public Item(String name, String description, int heal, boolean usable) {
        this.name = name;
        this.description = description;
        this.heal = heal;
        this.usable = usable;
    }

    public String getName() {
        return name;
    }

    public int getHeal() {
        return heal;
    }

    public boolean isUsable() {
        return usable;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Item)) return false;

        Item other = (Item) obj;
        return name.equalsIgnoreCase(other.name);
    }
}

class Hero {
    private String name;
    private int health;
    private int maxHealth;
    private Item[] inventory;
    private int itemCount;

    public Hero(String name) {
        this.name = name;
        this.health = 100;
        this.maxHealth = 100;
        this.inventory = new Item[10];
        this.itemCount = 0;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void takeDamage(int damage) {
        health -= damage;

        if (health < 0) {
            health = 0;
        }

        System.out.println("Вы получили " + damage + " урона.");
        System.out.println("Здоровье: " + health + "/" + maxHealth);
    }

    public void heal(int amount) {
        health += amount;

        if (health > maxHealth) {
            health = maxHealth;
        }

        System.out.println("Вы восстановили здоровье.");
        System.out.println("Здоровье: " + health + "/" + maxHealth);
    }

    public void addItem(Item item) {
        if (itemCount >= inventory.length) {
            Item[] newInventory = new Item[inventory.length * 2];

            for (int i = 0; i < inventory.length; i++) {
                newInventory[i] = inventory[i];
            }

            inventory = newInventory;
        }

        inventory[itemCount] = item;
        itemCount++;

        System.out.println("Предмет добавлен: " + item.getName());
    }

    public void showInventory() {
        System.out.println("\n--- ИНВЕНТАРЬ ---");

        if (itemCount == 0) {
            System.out.println("Инвентарь пуст.");
            return;
        }

        for (int i = 0; i < itemCount; i++) {
            System.out.println((i + 1) + ". " + inventory[i]);
        }
    }

    public Item findItem(String name) throws NotFoundException {
        for (int i = 0; i < itemCount; i++) {
            if (inventory[i].getName().equalsIgnoreCase(name)) {
                return inventory[i];
            }
        }

        throw new NotFoundException("Предмет не найден: " + name);
    }

    public void useItem(int number) throws NotFoundException {
        if (number < 1 || number > itemCount) {
            throw new NotFoundException("Такого предмета нет в инвентаре.");
        }

        Item item = inventory[number - 1];

        if (!item.isUsable()) {
            System.out.println("Этот предмет нельзя использовать.");
            return;
        }

        if (item.getHeal() > 0) {
            if (health == maxHealth) {
                System.out.println("Здоровье уже максимальное.");
                return;
            }

            heal(item.getHeal());
            removeItem(number - 1);
        }
    }

    public void removeItem(int index) {
        for (int i = index; i < itemCount - 1; i++) {
            inventory[i] = inventory[i + 1];
        }

        itemCount--;
        inventory[itemCount] = null;
    }

    public boolean hasItem(String name) {
        try {
            findItem(name);
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Герой: " + name + ", здоровье: " + health + "/" + maxHealth;
    }
}

class Location {
    private String name;
    private String description;
    private Location[] exits;
    private int exitCount;
    private Item item;
    private int damage;
    private boolean visited;

    public Location(String name, String description, int damage) {
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.exits = new Location[10];
        this.exitCount = 0;
        this.visited = false;
    }

    public String getName() {
        return name;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void addExit(Location location) {
        if (exitCount < exits.length) {
            exits[exitCount] = location;
            exitCount++;
        }
    }

    public void showExits() {
        System.out.println("\nДоступные переходы:");

        for (int i = 0; i < exitCount; i++) {
            System.out.println((i + 1) + ". " + exits[i].getName());
        }
    }

    public Location getExit(int number) throws NotFoundException {
        if (number < 1 || number > exitCount) {
            throw new NotFoundException("Такого перехода нет.");
        }

        return exits[number - 1];
    }

    public int getExitCount() {
        return exitCount;
    }

    public void showDescription() {
        System.out.println("\n=== " + name.toUpperCase() + " ===");
        System.out.println(description);

        if (item != null) {
            System.out.println("На земле лежит предмет: " + item);
        }

        if (damage > 0) {
            System.out.println("В этой локации опасно!");
        }

        visited = true;
    }

    @Override
    public String toString() {
        return name + " - " + description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Location)) return false;

        Location other = (Location) obj;
        return name.equalsIgnoreCase(other.name);
    }
}

abstract class Action {
    private String name;

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract void execute(Hero hero, Location location, Quest quest);
}

class SearchAction extends Action {
    public SearchAction() {
        super("Поиск предмета");
    }

    @Override
    public void execute(Hero hero, Location location, Quest quest) {
        if (location.getItem() == null) {
            System.out.println("Здесь больше ничего нет.");
        } else {
            hero.addItem(location.getItem());
            location.setItem(null);
        }
    }
}

class LookAction extends Action {
    public LookAction() {
        super("Осмотр");
    }

    @Override
    public void execute(Hero hero, Location location, Quest quest) {
        location.showDescription();
        System.out.println("\n" + hero);
    }
}

class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}

class Quest {
    private Hero hero;
    private Location[] locations;
    private int locationCount;
    private Location currentLocation;
    private boolean hasOpenedGate;
    private boolean questFinished;
    private int ending;

    private Action[] actions;

    public Quest() {
        hero = new Hero("Путешественник");
        locations = new Location[15];
        locationCount = 0;

        hasOpenedGate = false;
        questFinished = false;
        ending = 0;

        actions = new Action[2];
        actions[0] = new SearchAction();
        actions[1] = new LookAction();

        createWorld();
    }

    private void createWorld() {
        Location village = new Location(
                "Деревня",
                "Вы оказались в небольшой деревне. Жители рассказывают о древнем королевстве.",
                0
        );

        Location forest = new Location(
                "Лес",
                "Густой лес окружает вас. Среди деревьев слышны странные звуки.",
                5
        );

        Location river = new Location(
                "Река",
                "Перед вами глубокая река. Через нее можно пройти по старому мосту.",
                0
        );

        Location cave = new Location(
                "Пещера",
                "Темная пещера скрывает множество опасностей и сокровищ.",
                15
        );

        Location tower = new Location(
                "Заброшенная башня",
                "Старая башня почти разрушена. Возможно, здесь остались полезные вещи.",
                0
        );

        Location ruins = new Location(
                "Древние руины",
                "Остатки древнего города. На стенах видны непонятные надписи.",
                10
        );

        Location dungeon = new Location(
                "Подземелье",
                "Темное подземелье, где когда-то держали пленников.",
                20
        );

        Location castle = new Location(
                "Замок",
                "Огромный замок. За его воротами находится тронный зал.",
                0
        );

        Location throne = new Location(
                "Тронный зал",
                "Перед вами древний трон. Здесь решится судьба королевства.",
                0
        );

        addLocation(village);
        addLocation(forest);
        addLocation(river);
        addLocation(cave);
        addLocation(tower);
        addLocation(ruins);
        addLocation(dungeon);
        addLocation(castle);
        addLocation(throne);

        village.addExit(forest);

        forest.addExit(village);
        forest.addExit(river);
        forest.addExit(cave);
        forest.addExit(tower);

        river.addExit(forest);
        river.addExit(ruins);

        cave.addExit(forest);
        cave.addExit(dungeon);

        tower.addExit(forest);
        tower.addExit(ruins);

        ruins.addExit(river);
        ruins.addExit(tower);
        ruins.addExit(castle);

        dungeon.addExit(cave);
        dungeon.addExit(castle);

        castle.addExit(ruins);
        castle.addExit(dungeon);

        throne.addExit(castle);

        village.setItem(new Item(
                "Зелье",
                "Восстанавливает 30 здоровья.",
                30,
                true
        ));

        forest.setItem(new Item(
                "Меч",
                "Старинный меч для защиты.",
                0,
                false
        ));

        river.setItem(new Item(
                "Ключ",
                "Старинный ключ от ворот замка.",
                0,
                false
        ));

        cave.setItem(new Item(
                "Зелье",
                "Восстанавливает 30 здоровья.",
                30,
                true
        ));

        tower.setItem(new Item(
                "Кристалл",
                "Магический кристалл древних.",
                0,
                false
        ));

        ruins.setItem(new Item(
                "Древняя книга",
                "Книга с историей королевства.",
                0,
                false
        ));

        dungeon.setItem(new Item(
                "Амулет",
                "Амулет, способный защитить владельца.",
                0,
                false
        ));

        currentLocation = village;
    }

    private void addLocation(Location location) {
        if (locationCount == locations.length) {
            Location[] newLocations = new Location[locations.length * 2];

            for (int i = 0; i < locations.length; i++) {
                newLocations[i] = locations[i];
            }

            locations = newLocations;
        }

        locations[locationCount] = location;
        locationCount++;
    }

    // Главное меню
    public void start() {
        System.out.println("---");
        System.out.println("текстовый квест");
        System.out.println("---");
        System.out.println("Ваша задача - найти путь к замку");
        System.out.println("и раскрыть тайну древнего королевства.");

        currentLocation.showDescription();

        while (!questFinished && hero.isAlive()) {
            showMenu();

            System.out.print("\nВаш выбор: ");
            int choice = Main.readNumber();

            try {
                switch (choice) {
                    case 1:
                        move();
                        break;

                    case 2:
                        searchItem();
                        break;

                    case 3:
                        hero.showInventory();
                        break;

                    case 4:
                        useItem();
                        break;

                    case 5:
                        showStatus();
                        break;

                    case 6:
                        showAllLocations();
                        break;

                    case 7:
                        findItem();
                        break;

                    case 8:
                        changeLocationDescription();
                        break;

                    case 9:
                        deleteCurrentItem();
                        break;

                    case 0:
                        questFinished = true;
                        System.out.println("Вы покинули игру.");
                        break;

                    default:
                        System.out.println("Такого пункта меню нет.");
                }
            } catch (NotFoundException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        if (!hero.isAlive()) {
            System.out.println("\nВЫ ПОГИБЛИ!");
            System.out.println("Королевство осталось без спасителя.");
            System.out.println("Концовка 1: Поражение.");
        }
    }

    private void showMenu() {
        System.out.println("\n--- МЕНЮ ---");
        System.out.println("1. Переместиться в другую локацию");
        System.out.println("2. Подобрать предмет");
        System.out.println("3. Посмотреть инвентарь");
        System.out.println("4. Использовать предмет");
        System.out.println("5. Информация о герое");
        System.out.println("6. Показать все локации");
        System.out.println("7. Найти предмет в инвентаре");
        System.out.println("8. Изменить описание текущей локации");
        System.out.println("9. Удалить предмет из текущей локации");
        System.out.println("0. Выход");
    }

    private void move() throws NotFoundException {
        currentLocation.showExits();

        if (currentLocation.getExitCount() == 0) {
            System.out.println("Переходов нет.");
            return;
        }

        System.out.print("Куда отправиться? ");
        int number = Main.readNumber();

        Location next = currentLocation.getExit(number);

        if (next.getName().equals("Замок") && !hero.hasItem("Ключ")) {
            System.out.println("Ворота замка закрыты. Нужен ключ!");
            return;
        }

        if (next.getName().equals("Тронный зал")) {
            if (!hero.hasItem("Кристалл")) {
                System.out.println("Для входа нужен магический кристалл!");
                return;
            }

            finishQuest();
            return;
        }

        currentLocation = next;

        System.out.println("\nВы переместились: " + currentLocation.getName());

        currentLocation.showDescription();

        if (currentLocation.getDamage() > 0) {
            hero.takeDamage(currentLocation.getDamage());
        }
    }

    private void searchItem() {
        Action action = actions[0];
        action.execute(hero, currentLocation, this);
    }

    private void useItem() throws NotFoundException {
        hero.showInventory();

        System.out.print("Введите номер предмета: ");
        int number = Main.readNumber();

        hero.useItem(number);
    }

    private void showStatus() {
        System.out.println("\n" + hero);
        System.out.println("Текущая локация: " + currentLocation.getName());
    }

    private void showAllLocations() {
        System.out.println("\n--- ВСЕ ЛОКАЦИИ ---");

        for (int i = 0; i < locationCount; i++) {
            System.out.println(
                    (i + 1) + ". " + locations[i].getName()
                            + (locations[i].isVisited() ? " (посещена)" : " (не посещена)")
            );
        }
    }

    private void findItem() {
        System.out.print("Введите название предмета: ");
        String name = Main.scanner.nextLine();

        try {
            Item item = hero.findItem(name);
            System.out.println("Найден предмет: " + item);
        } catch (NotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void changeLocationDescription() {
        System.out.println("Изменение описания текущей локации.");
        System.out.println("Функция демонстрирует работу с данными локаций.");
        System.out.println("Название: " + currentLocation.getName());
        System.out.println("Для изменения описания перейдите в исходный код.");
    }

    private void deleteCurrentItem() {
        if (currentLocation.getItem() == null) {
            System.out.println("В этой локации нет предметов.");
        } else {
            System.out.println(
                    "Предмет " + currentLocation.getItem().getName()
                            + " удален из локации."
            );

            currentLocation.setItem(null);
        }
    }

    private void finishQuest() {
        questFinished = true;

        System.out.println("\n!!!!");
        System.out.println("Вы вошли в тронный зал!");
        System.out.println("Перед вами открывается древняя тайна.");
        System.out.println("!!!!");

        System.out.println("\nВыберите судьбу королевства:");
        System.out.println("1. Использовать кристалл и спасти королевство.");
        System.out.println("2. Забрать сокровища и покинуть замок.");
        System.out.println("3. Разрушить кристалл.");

        System.out.print("Ваш выбор: ");
        ending = Main.readNumber();

        switch (ending) {
            case 1:
                System.out.println("\nКристалл засиял ярким светом.");
                System.out.println("Проклятие исчезло, а королевство ожило.");
                System.out.println("Концовка 2: Спаситель королевства!");
                break;

            case 2:
                System.out.println("\nВы забрали сокровища и исчезли.");
                System.out.println("Королевство осталось забытым.");
                System.out.println("Концовка 3: Охотник за сокровищами.");
                break;

            case 3:
                System.out.println("\nКристалл разбился.");
                System.out.println("Замок начал разрушаться.");
                System.out.println("Концовка 4: Разрушение королевства.");
                break;

            default:
                System.out.println("\nВы не смогли принять решение.");
                System.out.println("Концовка 5: Неопределенная судьба.");
                break;
        }
    }
}
