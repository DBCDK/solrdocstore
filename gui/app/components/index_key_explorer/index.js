import React from 'react';
import Parent from './parent';

const IndexKeyExplorer  = ({item,HeaderComponentClass,ParentElementComponentClass,ElementComponentClass})=>{
    return Object.keys(item).sort().map((key) =>
        (item[key] instanceof Array) ?
            <ParentElementComponentClass
                key={key}
                name={key}
                list={item[key]}/> :
            <Parent
                key={key}
                HeaderComponentClass={HeaderComponentClass}
                ElementComponentClass={ElementComponentClass}
                name={key}
                children={item[key]}/>
    );
};

export default IndexKeyExplorer;