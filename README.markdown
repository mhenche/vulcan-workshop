# Vulcan Workshop

## Important notes

The `master` branch contains the skeleton for a Vulcan API module. If you need to see the code of any step during the workshop, follow the commits in the [`workshop`](https://github.com/ahdezma/vulcan-workshop/tree/workshop) branch.

## Steps:

1. Clone this repository.
2. Download the [latest Liferay Portal Community Edition](https://sourceforge.net/projects/lportal/files/Liferay%20Portal/7.0.4%20GA5/liferay-ce-portal-tomcat-7.0-ga5-20171018150113838.zip/download).
3. Unzip it next to `vulcan-workshop` and rename it to `bundles`.
3. Start it with:
    ```bash
    cd bundles/tomcat-8.0.32/bin
    ./catalina.sh jpda start
    tail -f ../logs/catalina.out
    ```
4. Once _Liferay Portal_ has started, leave it with the default configuration (**test** user, Hypersonic Database).
5. Deploy the contents of the `dependencies` folder inside `bundles/deploy`. This will start two things:
    - Demo Data Creators: they are a set of classes for automatic creation of demo content.
    - Vulcan Architect: the core of the library we will be using.
    ```bash
    mv dependencies/demo-data-creators/*.jar ../bundles/deploy
    mv dependencies/vulcan-architect/*.jar ../bundles/deploy
    ```
6. Use your favourite REST client to perform a request to `http://localhost:8080/o/api`. You should receive a JSON with the following:
    ```json
    {
        "resources": {}
    }
    ```
7. You are now ready to continue with the workshop!